/*
	综合MessageStatsReport.java和DeliveredMessagesReport.java
*/
package report;

import java.sql.Array;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Delayed;

import core.DTNHost;
import core.Message;
import core.MessageListener;
import core.Settings;

/**
 * Report information about all delivered messages. Messages created during
 * the warm up period are ignored.
 * For output syntax, see {@link #HEADER}.
 */

/*
    Created				:	产生的消息数目
    Started				:	有多少新创建的消息被转发
    Relayed				:	被成功转发的消息数目（中间节点的转发）
    Dropped				:	被丢弃的消息数目（链接断开，消息的time-to-live到期了，缓冲区满了）
	Removed				:	进入消息队列前移除数
    Aborted				:	中止失败的消息个数
    Delivered			:	投递成功的消息个数
	ResponseDelivered	:	成功投递的回复数量
*/


public class MyReport extends Report implements MessageListener {
	public static String SPACE = "\t";

	public static final String SETTINGS_NAMESPACE = "Events";
	public static final String NROF_SETTING = "nrof";
	public static final String PREFIX_SETTING = "prefix";

	public static final double TIME = 60.0 * 30;
	public int POS;

	private Map<String, Double> creationTimes;
	//private List<Double> latencies;
	private List<Integer> hopCounts;
	private List<Double> msgBufferTime;
	private List<Double> rtt; // round trip times

	private Map<String, Integer> GroupMap;
	private List<String> GroupName;
	int[] nrofCreated; //消息总数
	int[] nrofStarted; //副本数
	int[] nrofRelayed; //转发数
	int[] nrofDelivered; //成功数
	int[] nrofResponseReqCreated;
	int[] nrofResponseDelivered;
	double[] latencies;

	private int GroupNum;

	private String[] ss;

	/**
	 * Constructor.
	 */
	public MyReport() {
		init();
	}

	@Override
	public void init() {
		super.init();

		this.creationTimes = new HashMap<String, Double>();
		//this.latencies = new ArrayList<Double>();
		this.msgBufferTime = new ArrayList<Double>();
		this.hopCounts = new ArrayList<Integer>();
		this.rtt = new ArrayList<Double>();

		this.GroupMap = new HashMap<String, Integer>();
		this.GroupName = new ArrayList<String>();

		Settings settings = new Settings(SETTINGS_NAMESPACE);
		int GroupNum = settings.getInt(NROF_SETTING);
		for (int i = 1; i <= GroupNum; i++) {
			Settings s = new Settings(SETTINGS_NAMESPACE + i);
			String name = s.getSetting(PREFIX_SETTING);
			GroupMap.put(name, i);
			GroupName.add(name);
		}

		this.nrofCreated = new int[GroupNum + 1];
		this.nrofStarted = new int[GroupNum + 1];
		this.nrofRelayed = new int[GroupNum + 1];
		this.nrofDelivered = new int[GroupNum + 1];
		this.nrofResponseReqCreated = new int[GroupNum + 1];
		this.nrofResponseDelivered = new int[GroupNum + 1];
		this.latencies = new double[GroupNum+1];

		this.ss = new String[3];
		for(int i=0;i<3;i++)
			ss[i] = "";

		this.POS = 0;
	}

	/**
	 * Method is called when a new message is created
	 * @param m Message that was created
	 */
	public void newMessage(Message m) {
		if (isWarmup()) {
			addWarmupID(m.getId());
			return;
		}

		this.creationTimes.put(m.getId(), getSimTime());
		this.nrofCreated[GroupMap.get(noNumber(m.getId()))]++;

		if (m.getResponseSize() > 0) {
			this.nrofResponseReqCreated[GroupMap.get(noNumber(m.getId()))]++;
		}
	}

	/**
	 * Method is called when a message's transfer is started
	 * @param m The message that is going to be transferred
	 * @param from Node where the message is transferred from
	 * @param to Node where the message is transferred to
	 */
	public void messageTransferStarted(Message m, DTNHost from, DTNHost to) {
		if (isWarmupID(m.getId())) {
			return;
		}
		this.nrofStarted[GroupMap.get(noNumber(m.getId()))]++;
	}

	/**
	 * Method is called when a message is deleted
	 * @param m The message that was deleted
	 * @param where The host where the message was deleted
	 * @param dropped True if the message was dropped, false if removed
	 */
	public void messageDeleted(Message m, DTNHost where, boolean dropped) {

	}

	/**
	 * Method is called when a message's transfer was aborted before
	 * it finished
	 * @param m The message that was being transferred
	 * @param from Node where the message was being transferred from
	 * @param to Node where the message was being transferred to
	 */
	public void messageTransferAborted(Message m, DTNHost from, DTNHost to) {

	}

	/**
	 * Method is called when a message is successfully transferred from
	 * a node to another.
	 * @param m The message that was transferred
	 * @param from Node where the message was transferred from
	 * @param to Node where the message was transferred to
	 * @param firstDelivery Was the target node final destination of the message
	 * and received this message for the first time.
	 */
	public void messageTransferred(Message m, DTNHost from, DTNHost to, boolean firstDelivery) {
		if (isWarmupID(m.getId())) {
			return;
		}

		this.nrofRelayed[GroupMap.get(noNumber(m.getId()))]++;
		if (firstDelivery) {
			this.latencies[GroupMap.get(noNumber(m.getId()))] += (getSimTime() - this.creationTimes.get(m.getId()));
			this.nrofDelivered[GroupMap.get(noNumber(m.getId()))]++;
			this.hopCounts.add(m.getHops().size() - 1);

			if (m.isResponse()) {
				this.rtt.add(getSimTime() - m.getRequest().getCreationTime());
				this.nrofResponseDelivered[GroupMap.get(noNumber(m.getId()))]++;
			}
		}

		if (this.POS * TIME <= getSimTime()) {
			this.POS++;
			NewLine(format(getSimTime()));
		}
	}



	public void NewLine(String time) {
		int totCreated = 0;
		int totStarted = 0;
		int totDelivered = 0;
		double totdeliveryProb = 0;
		double totUseProb = 0;
		double totTimeProb = 0;
		double totTimeSum = 0;

		this.ss[0] += time;
		this.ss[1] += time;
		this.ss[2] += time;

		for (String name : GroupName) {
			int pos = GroupMap.get(name);

			int Created = nrofCreated[pos];
			int Started = nrofStarted[pos];
			int Delivered = nrofDelivered[pos];
			double deliveryProb = 0;
			double UseProb = 0;
			double TimeProb = 0;

			totCreated += Created;
			totStarted += Started;
			totDelivered += Delivered;
			totTimeSum += latencies[pos];

			if (Created > 0)
				deliveryProb = (1.0 * Delivered) / Created;
			if (Created > 0)
				UseProb = (1.0 * Started) / Created;
			if (Delivered > 0)
				TimeProb = latencies[pos] / Delivered;	

			this.ss[0] += SPACE + format(deliveryProb);
			this.ss[1] += SPACE + format(UseProb);
			this.ss[2] += SPACE + format(TimeProb);
		}

		if (totCreated > 0)
			totdeliveryProb = (1.0 * totDelivered) / totCreated;
		if (totStarted > 0)
			totUseProb = (1.0 * totStarted) / totCreated;
		if (totDelivered > 0)
			totTimeProb = totTimeSum / totDelivered;	

		this.ss[0] += SPACE + format(totdeliveryProb);
		this.ss[0] += "\n";

		this.ss[1] += SPACE + format(totUseProb);
		this.ss[1] += "\n";

		this.ss[2] += SPACE + format(totTimeProb);
		this.ss[2] += "\n";


	}

	String noNumber(String str) {
		return str.replaceAll("\\d+", "");
	}

	@Override
	public void done() {
		for(int i=0;i<3;i++){
			String s;
			s = "Time";
			for (String name : GroupName)
				s += SPACE + name;
			s += SPACE + "Tot";
			write(s);
			write(ss[i]);
			write("\n");
		}


		write("\n\n\n");

		write("Type" + SPACE + "Created" + SPACE + "Started" + SPACE + "Relayed" + SPACE + "Delivered" + SPACE
				+ "deliveryProb" + SPACE +"开销率" + SPACE + "平均延迟");

		int totCreated = 0;
		int totStarted = 0;
		int totRelayed = 0;
		int totDelivered = 0;
		double totdeliveryProb = 0;
		double totUseProb = 0;
		double totTimeProb = 0;
		double totTimeSum = 0;

		for (String name : GroupName) {
			int pos = GroupMap.get(name);

			int Created = nrofCreated[pos];
			int Started = nrofStarted[pos];
			int Relayed = nrofRelayed[pos];
			int Delivered = nrofDelivered[pos];
			double deliveryProb = 0;
			double UseProb = 0;
			double TimeProb = 0;

			totCreated += Created;
			totStarted += Started;
			totRelayed += Relayed;
			totDelivered += Delivered;
			totTimeSum += latencies[pos];

			if (Created > 0)
				deliveryProb = (1.0 * Delivered) / Created;
			if (Created > 0)
				UseProb = (1.0 * Started) / Created;
			if (Delivered > 0)
				TimeProb = latencies[pos] / Delivered;	


			write(name + SPACE + Created + SPACE + Started + SPACE + Relayed + SPACE + Delivered + SPACE
					+ format(deliveryProb) + SPACE + format(UseProb) + SPACE + format(TimeProb));
		}

		if (totCreated > 0)
			totdeliveryProb = (1.0 * totDelivered) / totCreated;
		if (totStarted > 0)
			totUseProb = (1.0 * totStarted) / totCreated;
		if (totDelivered > 0)
			totTimeProb = totTimeSum / totDelivered;	

		write("Tol" + SPACE + totCreated + SPACE + totStarted + SPACE + totRelayed + SPACE + totDelivered + SPACE
				+ format(totdeliveryProb) + SPACE + format(totUseProb) + SPACE + format(totTimeProb));
		super.done();
	}

	public void s(String txt) {
		System.out.println(txt);
	}

	public void s(int txt) {
		System.out.printf("%d\n", txt);
	}
}