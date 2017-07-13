/*
	修改自MessageEventGenerator.java
	改变了传的参数
 */
package input;

import java.util.Random;

import core.Settings;
import core.SettingsError;

public class MessageWithCopiesEventGenerator extends MessageEventGenerator {
	public static final String MESSAGE_INITCOPIES = "initCopies";
	protected int initCopies;

	public MessageWithCopiesEventGenerator(Settings s){
		super(s);
        this.initCopies = s.getInt(MESSAGE_INITCOPIES);
	}

	public ExternalEvent nextEvent() {
		int responseSize = 0; /* zero stands for one way messages */
		int msgSize;
		int interval;
		int from;
		int to;

		/* Get two *different* nodes randomly from the host ranges */
		from = drawHostAddress(this.hostRange);
		to = drawToAddress(hostRange, from);

		msgSize = drawMessageSize();
		interval = drawNextEventTimeDiff();

		/* Create event and advance to next event */
		MessageWithCopiesCreateEvent mce = new MessageWithCopiesCreateEvent(from, to, this.getID(),
				msgSize, responseSize, this.nextEventsTime,initCopies);
		this.nextEventsTime += interval;

		if (this.msgTime != null && this.nextEventsTime > this.msgTime[1]) {
			/* next event would be later than the end time */
			this.nextEventsTime = Double.MAX_VALUE;
		}

		return mce;
	}
}
