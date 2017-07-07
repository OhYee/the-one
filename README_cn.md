# ONE v1.6.0 - 自述文件  
--------  
  
ONE是一个机会网络环境模拟器，它提供了一个用于生成移动性跟踪的强大工具，运行DTN消息传递模拟不同的路由协议，并可视化两者实时交互式模拟完成后的结果。  
  
  
# 快速开始  
--------  
  
## 编译  
---------  
  
您可以使用包含的 `compile.bat` 从源代码编译一个脚本。  
这在Windows和Unix/Linux环境中都能工作  
（需要Java 6 JDK或更高版本。）  
  
如果要使用Eclipse来编译ONE，则需要1.1.0以上版本。  
在项目的构建路径中包含位于 `lib` 文件夹中的jar库。  
要将他们加入到你的Eclipse中，你需要有一个Eclipse Java项目，其根文件夹是您提取的ONE文件夹，请执行以下操作：  
   
从菜单中选择：项目 - >属性 - > Java构建路径  
转到 “库” 选项卡  
点击 “添加JAR ...”  
在 “lib” 文件夹下选择 `DTNConsoleConnection.jar`  
以相同的方式添加 `ECLA.jar`  
按 “确定”。  
  
现在Eclipse应该可以在没有警告的情况下正常编译。  
  
  
## 运行  
-------  
  
可以使用包含的`one.bat`（Windows）或`one.sh`（对  
Linux / Unix）。以下示例假定您正在使用Linux/Unix  
脚本（在Windows环境下只需将`./one.sh`替换为`one.bat`）。  
  
概要：  
`./one.sh [-b runco​​unt] [conf-files]`  
  
选项：  
`-b` 以批处理模式运行模拟。不启动GUI的情况下打印有关终端进展的信息。  
该选项后必须有批处理模式的运行次数或者运行范围，用冒号分隔（例如，值2：4将执行运行2，3和4）。routing modulesrouting modules  
有关详细信息，请参阅“运行索引”一节。  
    
参数：  
conf文件：读取模拟参数的配置文件名。  
可以定义任意数量的配置文件按照命令行中给出的顺序读取。后面的配置文件中的值覆盖早期配置文件中的值。  
  
  
## 配置  
--------  
  
所有仿真参数均通过配置文件给出。  
这些文件是包含键值对的普通文本文件。  
大部分语法变量是： `Namespace.key = value`  
  
即，键通常以命名空间为前缀，后跟一个点  
最后是键名。键和值通过等号分隔。  
命名空间以大写字母开头，命名空间和键采用驼峰命名法（区分大小写）。  
命名空间限定了设置生效的范围。  
  
大部分命名空间等于它们被读取的类名，特别是运动模型(movement models)，报告模块(report modules)和路由模块(routing modules)。  
  
在某些情况下，命名空间由用户定义。例如网络接口用户可以使用任何标识符。对于这种用户自己定义的网络接口就可以使用接口名称作为命名空间。  
  
数值使用`.`作为小数分隔符，可以后缀千（k）兆（M）或千兆（G）后缀。布尔设置接受“true”，“false”，“0”和“1”作为值。  
  
许多设置定义了外部数据文件的路径。路径可以是相对路径或绝对路径，但目录分隔符无论在Unix还是Windows中必须使用`/`。  
  
一些变量包含逗号分隔的值，对于它们来说，语法是：  
`Namespace.key = value1, value2, value3`等  
  
对于运行索引值，语法为：  
`Namespace.key = [run1value; run2value; run3value;etc]`  
即，所有值在括号中给出，用分号分隔,也可以用逗号分隔。  
有关运行索引的更多信息，请转到“运行索引”部分。  
  
设置文件也可以包含注释。注释行必须以`＃`开头  
读取设置时，跳过该行的其余部分。这个可以也可以轻松禁用某些设置。  
  
一些值（脚本或者报告名）支持变量。通过此功能，您可以动态构建脚本名称，这在使用索引运行时特别有用。  
  
只需在变量前后分别放两个 `%` 即可。这些变量会被替换来自配置文件的当前设置值。  
例子见`snw_comparison_settings.txt`为例。  
  
文件`default_settings.txt`，如果存在，总是读取而另一个  
作为参数给出的配置文件可以定义更多的设置或覆盖以前的文件中的一些（甚至全部）设置。  
这个想法是这样的您可以在早期的文件中定义所有常见的设置所有的模拟和运行不同的，具体的模拟使用不同的配置文件。  
  
  
## 运行索引  
------------  
  
运行索引是一个允许您运行大量不同配置的数据而只使用一个配置文件的配置。  
实现这个功能需要你按照上面的格式提供一个一个数组作为每次运行时的状态变量。  
  
例如，如果要用运动模型的五个不同的随机数发生器运行模拟，您可以在设置文件中定义以下内容：  
`MovementModel.rngSeed = [1; 2; 3; 4; 5]`  
  
现在，使用命令运行模拟：  
`./one.sh -b 5 my_config.txt`  
你将首先使用seed 1（运行索引0）运行，然后再运行useseed 2等。  
请注意，你必须使用批处理模式（-b选项）运行它，如果你想使用不同的值。  
  
没有批处理模式标志，第一个参数（如果是数字）是在GUI模式下运行时使用的运行索引。  
Run索引环绕：used值是index（runIndex％arrayLength）处的值。由于包装，您可以轻松地运行大量的精子。  
  
例如，如果定义了两个键值对：  
`key1 = [1; 2]`  
`key2 = [a; b; c]`  
并使用运行索引计数6运行模拟，您将得到两个值（1，a; 2，b; 1，c; 2，a; 1，b; 2，c）的排列。  
  
这自然可以使用任何数量的阵列。只需确保所有数组大小的最小共同点为1（例如，使用大小为素数的数组）。除非您不想要所有的排列，而需要对某些值进行匹配。  
  
## 运动模型  
--------  
  
运动模型控制节点在模拟中移动的方式。  
它们为节点提供坐标，速度和暂停时间。  
  
基本功能包含例如随机路点，基于地图的移动，基于最短路径图的移动，地图路线移动和外部移动。  
除外部移动外，所有这些型号都具有可配置的速度和暂停时间。可以给出最小值和最大值，运动模型绘制均匀分布的随机值在给定范围内。  
  
在外部运动模型中，根据给定的数据设置速度和停顿时间。  
当节点使用随机航点运动模型（RandomWaypoint）时，在模拟区域中给出随机坐标。  
节点以恒定的速度直接移动到给定的目的地，暂停一段时间，然后获得新的目的地。  
这在整个模拟过程中节点持续沿着这些之字形路径移动。  
  
基于地图的运动模型（MapBasedMovement）将节点运动约束到预定义的路径中。  
我们可以定义不同类型的路径，并且可以定义所有节点组的有效路径。  
基于地图的运动模型结点最初分布在任意两个相邻节点之间（即路径上），然后节点开始移动到下一个结点，从而可以防止车辆在室内或行人路上行驶。  
当节点到达下一个地图节点时，它会随机选择下一个相邻的地图节点，但是只有在唯一的选择（即避免回到它所在的地方）的情况下才选择它的上一个节点。  
一旦节点移动了10~100个地图节点，它将暂停一段时间，然后再次开始移动。  
  
复杂的基于地图的运动模型（ShortestPathMapBasedMovement）使用Dijkstra的最短路径算法来找到其通过地图区域的方式。  
一旦一个节点到达其目的地并等待了设定的暂停时间，则选择一个新的随机映射节点，节点使用最短路径移动到该节点。  
  
对于基于最短路径的运动模型，地图数据也可以包含兴趣点（POI）。  
POI代替为下一个目的地选择任何随机映射节点，移动模型可以被配置为给予具有可配置概率的属于某个POI组的POI。  
可以有无限量的POI组，所有组可以包含任何数量的POI。所有节点组对于所有POI组可以具有不同的概率。  
POI可用于建模商店，餐馆和旅游景点。基于路由的移动模型（MapRouteMovement）可用于对遵循某些路线的节点进行建模，例如公共汽车或电车线路。  
必须定义路由上的停靠点，然后使用该路由的节点从停止位置移动到使用最短路径停止，并在停止时停止配置的时间。  
所有移动模型还可以决定节点何时处于活动状态（移动并且可以连接到），而不是。  
对于所有型号，除了外部移动，可以给出多个模拟时间间隔，并且该组中的节点将仅在那些时间内处于活动状态。  
  
所有基于地图的模型都使用格式为“Well Known Text”（WKT）格式的子集的文件获取输入数据。用于地图路径数据的解析器支持LINESTRING和MULTILINESTRING WKT文件的指令。  
对于点数据（例如POI），还支持POINT指令。 （MULTI）LINESTRING中的相邻节点被认为形成路径，并且如果某些行包含具有完全相同协调的一些顶点es，路径从那些地方连接起来（这是你如何创建交点）。   
WKT文件可以使用任何合适的地理信息系统（GIS）程序从现实世界地图数据中进行编辑和生成。  
使用免费的基于Java的OpenJUMP GIS程序对包含在模拟器分发中的地图数据进行转换和编辑。  
通过将属于不同类型的路径存储到不同的文件来定义不同的地图类型。  
兴趣点仅用WKT POINT指令定义，POI组通过将属于某个组的所有POI存储在同一文件中来定义。  
所有POI也必须是地图数据的一部分，因此可以使用路径进行访问。通过LINESTRING定义路线的停靠点，并按照LINESTRING中显示的顺序遍历停靠点。  
一个WKT文件可以包含多个路由，它们以与文件中出现的顺序相同的顺序提供给节点。  
  
使用外部移动数据（ExternalMovement）的实验运动模型从文件读取时间戳节点位置，并在模拟中移动节点。  
  
有关格式的详细信息，请参阅输入包中`ExternalMovementReader`类的javadocs。用于TRANSIMS数据的合适的实验转换脚本（`transimsParser.pl`）包含在toolkit文件夹中。  
  
使用“movementModel”设置为每个节点组定义要使用的运动模型。设置值必须是运动包中有效的运动模型类名。  
  
所有运动模型中常见的设置在`MovementModel`类中读取，运动模型的具体设置在相应的类中读取。  
  
有关详细信息，请参阅javadoc文档和示例配置文件。  
  
  
## 路由模块和消息创建  
--------  
  
路由模块定义消息的处理方式模拟。  
包括六个基本的主动路由模块（First Contact，Epidemic，Spray and Wait，Direct Delivery，PRoPHET和MaxProp）以及用于外部路由模拟的被动路由器。  
主动路由模块是用于DTN路由的众所周知的路由算法的实现。还有最新版本中包含的这些型号和几种不同型号的变体。  
  
有关详细信息，请参阅路由包中的类。  
  
无线路由器特别用于与其他（DTN）路由模拟器交互或运行不需要任何路由功能的仿真。除非由外部事件指挥，否则路由器不会执行任何操作。  
  
这些外部事件由实现EventQueue接口的类提供给模拟器。  
有两个基本类可以用作消息事件的源：`ExternalEventsQueue`和`MessageEventGenerator`。  
  
前者可以使用适合的脚本（例如，toolkit文件夹中的createCreates.pl脚本）或通过将dtnsim2的输出转换为合适的形式，从手动创建的文件中读取事件。  
有关格式的详细信息，请参阅`InputEventsReader`类。   
  
`MessageEventGenerator` 是一个简单的消息生成器类，可以使用可配置的消息创建间隔，消息大小和源/目标终端范围来创建均匀分布的消息创建模式。  
可以使用`MessageBurstGenerator`和`One {From，To}` `EveryMessageGenerator`类创建更具体的消息传递场景。  
有关详细信息，请参见javadoc。  
  
该工具包文件夹包含一个用于dtnsim2输出的实验性解析器脚本（`dtnsim2parser.pl`）（以前是一个更有能力的基于Java的解析器，但丢弃了这个更容易扩展的脚本）。  
脚本需要几个补丁才能使用dtnsim2的代码，并且可以从`toolkit/dtnsim2patches`文件夹中找到这些代码。  
要使用的路由模块是根据设置为“router”的每个节点组定义的。  
所有路由器无法正常交互（例如，PRoPHET路由器只能与其他PRoPHET路由器配合使用），所以通常对所有组使用相同（或兼容的）路由器是有意义的。  
  
## 报告  
-------  
  
可以使用报告创建仿真运行的摘要数据，连接和消息的详细数据，适用于使用Graphviz（创建图形）进行后处理的文件以及与其他程序的接口。  
  
有关详细信息，请参阅报告包类的javadoc。  
  
对于任何模拟运行，可以存在任意数量的报告，并使用 `Report.nrofReports` 设置定义要加载的报告数。  
报告类名称使用“Report.reportN”设置定义，其中N是从1开始的整数值。设置值必须是报表包中有效的报表类名称。  
必须使用 `Report.reportDir -setting` 来定义所有报告的输出目录（可以使用“输出”设置在每个报告类中覆盖）。  
如果没有为报告类设置“输出”设置，则生成的报告文件名称为 `ReportClassName_ScenarioName.txt` 。  
所有报告都有许多可配置的设置，可以使用`ReportClassName.settingKey -syntax`进行定义。  
  
看javaReport类和具体报告类的文档（查找“设置id”定义）。  
  
## 终端组(Host groups)  
-----------  
  
终端组是共享运动和路由模块设置的终端（节点）组。  
不同的组可以具有不同的设置值，并且这种方式可以表示不同类型的节点。  
可以在“组”命名空间中定义基本设置，不同的节点组可以覆盖这些设置或在其特定命名空间（Group1，Group2等）中定义新设置。  
  
## 设置  
------------  
  
有很多设置要配置;超过有意义的在这里表达。  
  
有关详细信息，请参阅javadocs的特别报告，路由和移动模型类。另请参阅包括设置文件的示例。  
  
或许最重要的设置如下。  
  
### 场景设置  
--------  
  
`Scenario.name` 场景的名字  
默认情况下，所有报告文件都带有这个前缀。  
  
`Scenario.simulateConnections` 是否模拟连接。  
如果您只对运动模型感兴趣，可以禁用此功能以获得更快的模拟。  
  
`Scenario.updateInterval` 模拟时间步长  
增加将获得更快的速度，但是会失去一定的精度。  
推荐0.1~2  
  
`Scenario.endTime` 要模拟几秒  
  
`Scenario.nrofHostGroups` 模拟中的终端组数。  
  
### 接口设置（用于定义节点可能具有的接口）  
--------  
  
`type`这个接口使用了什么类（从interfaces-directory）  
  
剩下的设置是class  
例如：  
`transmitRange` Range（米）的接口.  
  
`transmitSpeed` Transmit接口的速度（每秒字节数）.  
  
### Host组设置（用于Group或GroupN命名空间）：  
--------  
  
`groupID` Group的标识符（字符串或字符）。  
用作GUI和报告中显示的终端名的前缀。  
终端的全名是 `groupID + networkAddress`.  
  
`nrofHosts` 此组中的终端数量.  
  
`nrofInterfaces` 这个组中使用的接口的数目  
  
`interfaceX` 接口应该被用作接口号X  
  
`movementModel` 该组中结点的运动模式  
必须是一个有效的类（一个是MovementModel类的子类）名称。  
  
`waitTime` 等待时间间隔（秒）的最小和最大（两个逗号分隔的十进制值）。  
定义到达目的路径的目的地后，节点在同一个地方停留多长时间。每个停止点使用间隔内的新随机值。  
默认值为`0,0`.  
  
`speed` 速度间隔（m / s）的最小值和最大值（两个逗号分隔的十进制值）。定义节点移动速度。每个新路径都使用一个新的随机值。  
默认值为`1,1`.  
  
`bufferSize` Size节点的消息缓冲区（bytes）。  
当缓冲区已满时，节点不能接受任何更多的消息，除非它从缓冲区中删除一些旧消息  
  
`router` 路由器模块。  
必须是一个来自路由包的有效的类（MessageRouter类的子类）名称.  
  
`activeTimes` 当组中的节点激活的时间间隔（逗号分隔的模拟时间值元组：start1，end1，start2，end2，...）。  
如果没有定义间隔，则节点始终处于活动状态。  
  
`msgTtl` 存活时间（分钟）。  
节点（具有活动路由模块）每隔一分钟检查一些消息的TTL是否已过期并丢弃过期消息。  
如果没有定义TTL，则使用无限制的TTL。  
  
组和运动模型的具体设置（仅对某些运动模型有意义）：  
  
`POI` 兴趣点索引及概率（逗号分隔索引概率元组：poiIndex1，poiProb1，poiIndex2， poiProb2，...）。  
索引是整数，概率是0.0-1.0的十进制值。  
设置定义了该终端组中的节点可以选择目的地的POI组以及选择某个POI组的概率。  
例如，使用概率poiProb1选择来自POI文件1中定义的组（由`PointsOfInterest.poiFile1`设置定义）中的（随机）POI。  
如果所有概率的总和小于1.0，则为下一个目标选择任意随机映射节点的概率为（1.0 - theSumOfProbabilities）。  
设置只能使用基于ShortestPathMapBasedMovement的运动模型。   
  
`okMaps`对于组（逗号分隔的整数列表），映射节点类型（指映射文件索引）都可以。  
节点不能通过对它们不行的地图节点传播。  
默认情况下，所有地图节点都可以。设置可以与任何基于MapBasedMovent的移动模型一起使用.  
  
`routeFile`如果使用MapRouteMovement运动模型，此设置定义从中读取路线的路由文件（路径）。  
路由文件应包含LINESTRING WKT指令。  
一个LINESTRING中的每个顶点代表路由上的一个停止点.  
  
`routeType` 如果使用了MapRouteMovement运动模型。此设置定义路由类型。  
类型可以是circular(循环)或ping-pong(往复)。  
请参阅`movement.map.MapRoute`类的详细信息。  
  
### 运动模型设置：  
--------  
  
`MovementModel.rngSeed` 所有运动模型的种子随机数生成器。如果种子和所有运动模型的相关设置保持不变，所有节点都应该以不同的模拟方式移动相同的方式（使用相同的目的地和速度和等待时间值）.  
  
`MovementModel.worldSize`以米为单位的模拟世界的大小（两个逗号分隔的值：width，height）.  
  
`PointsOfInterest.poiFileN`对于基于ShortestPathMapBasedMovement的移动模型，此设置定义读取POI坐标的WKT文件。使用POINT WKT指令定义POI坐标。设置结束时的“N”必须是正整数（即poiFile1，poiFile2，...）。“MapBasedMovement.nrofMapFiles”在设置文件中要查找多少个地图文件设置。   
  
`MapBasedMovement.mapFileN`Path到第N个地图文件（“N”必须是正整数）。在配置文件中必须至少存在nrofMapFiles分离的文件。所有地图文件必须是具有LINESTRING和/或MULTILINESTRING WKT指令的WKT文件。地图文件也可以包含POINT指令，但是这些指令将被跳过。这样，同一个文件可以用于POI和地图数据。默认情况下，地图坐标被翻译，使得地图的左上角在坐标点（0,0）处。 Y坐标在翻译之前被镜像，以便地图的北部在播放视野中指向。所有POI和路由文件也被转换为与地图数据转换相匹配。  
  
### 报告设置：  
--------  
  
`Report.nrofReports`需要加载多个报告模块。模块名称通过设置“Report.report1”，“Report.report2”等定义。可以为所有报告（使用报告名称空间）或仅针对某些报告（使用ReportN名称空间）定义以下报告设置。报告。   
  
`reportDir`在哪里存储报告输出文件。可以是绝对路径或相对于开始模拟的路径。如果该目录不存在，它将被创建.  
  
`Report.warmup` 预热时段的长度（从开始的模拟秒）。报告模块在热身之前应该放弃新的事件。  
该行为是报告模块具体，因此请查看不同报告模块的（java）文档的详细信息。  
  
### 事件生成器设置：  
--------  
  
`Events.nrof` 仿真器需要加载多少时间生成器。  
事件生成器的具体设置（见下文）在EventsN命名空间中定义（所以Events1.settingName配置第一个事件生成器的设置等）。  
  
`EventsN.class` 要加载的事件生成器类名（例如ExternalEventsQueue或MessageEventGenerator）。  
必须从输入包中找到该类。  
对于`ExternalEventsQueue`，必须至少定义外部事件文件的路径（使用设置“filePath”）。  
  
有关不同外部事件的信息，请参阅 `input.StandardEventsReader` 类“javadoc”。  
  
### 其他设置：  
--------  
  
`Optimization.randomizeUpdateOrder` 将调用节点的更新方法的顺序为randomized.Call更新将导致节点检查其连接并更新其路由模块。如果设置为false，节点更新顺序与其网络地址顺序相同。随机化，顺序在每个时间步长是不同的。  
`Optimization.cellSizeMult` 调整内存消耗和模拟速度之间的权衡。  
特别适用于大地图。  
请参阅`ConnectivityOptimizer`类了解详细信息.  
  
# GUI  
--------  
  
  
GUI的主窗口分为三部分。  
  
主要部分包含播放视野（显示节点移动）和模拟以及GUI控制和信息。  
正确的部分用于选择节点，下半部分用于记录和断点。  
主要部分是用于模拟和GUI控制的部分。  
第一个字段显示当前的模拟时间。下一个字段显示模拟速度（每秒模拟秒数）。  
以下四个按钮用于暂停，逐步，快进，快进模拟到给定时间。  
多次按下步进按钮，逐步运行模拟。快进（FFW）可以用来跳过模拟的不感兴趣。  
在FFW中，GUI更新速度设置为较大的值。   
Nextdrop down用于控制GUI更新速度。  
速度1意味着GUI在每个模拟的第二次更新。速度10意味着GUI每10秒钟才更新一次等等。负数值会减慢模拟速度。下面的下拉菜单控制缩放因子。  
最后一个按钮将当前视图保存为png图像。  
  
中间部分，即播放视图，显示节点位置，地图路径，节点标识符，节点之间的连接等。所有节点都显示为小矩形，其无线电范围显示为环绕节点的绿圈。  
节点的组标识符和网络地址（数字）显示在每个节点的旁边。如果一个节点正在携带消息，消息由绿色或蓝色填充的矩形表示。  
如果nodecarries超过10个消息，另一列矩形绘制10个消息，但每个其他矩形现在是红色。您可以通过在播放字段上单击鼠标按钮将视图居中到任何地方。   
Zoomfactor也可以使用鼠标滚轮在播放视图的顶部进行更改。  
  
主窗口的右侧部分用于选择节点进行仔细检查。单击按钮将显示主要部分下部的节点信息。  
通过从下拉菜单中选择节点携带的任何一个（如果有的话）显示。按“路由信息”按钮打开一个新窗口，其中显示有关路由模块的信息。当选择一个节点时，播放视图视图以该节点为中心，节点正在运行的当前路径以红色显示。  
  
记录（最低部分）如果分为两个部分，则控制和日志。从控制部分，您可以选择在日志中显示什么样的消息。您还可以定义在特定类型的事件上是否应暂停仿真（使用“暂停”列中的复选框）。  
日志部分显示时间戳事件。  
日志消息中的所有节点和消息名称都是按钮，您可以通过单击按钮获取有关它们的更多信息.  
  
# DTN2参考实现连接  
--------  
  
  
DTN2连接允许捆绑包在ONE和任何数量的DTN2路由器之间传递。这通过DTN2的外部融合层接口完成。  
当DTN2连接被启用时，将连接到作为外部融合层适配器的dtnd路由器。   
ONE还将通过控制台连接自动配置一个链接和捆绑路由到达模拟器。  
  
当从dtnd收到捆绑包时，ONE尝试将目标EIDagainst匹配到配置文件中配置的正则表达式（参见下面的DTN2Connectivity配置文件） 。  
对于每个匹配节点，创建一个amessage的副本并将其路由到ONE中。当捆绑包到达其destinationinside ONE时，它被传递到附加到节点的dtnd路由器实例。捆绑有效载荷的存储在“bundles”目录中。  
  
要启用此功能，必须采取以下步骤：  
1. 必须对DTN2进行编译和配置，并启用ECL支持。必须将DTN  
2. Events事件生成器配置为1作为事件类加载。   
3. DTN2Reporter必须配置为一个作为报告类加载。   
4. DTN2连接配置文件必须配置为DTN2.configFile才能  
  
开始模拟：  
1. 启动所有的dtnd路由器实例。  
2. 启动ONE.  
  
## Example配置（上述2-4）  
---------------------------------   
```  
Events.nrof = 1   
Events1 .class = DTN2Events  
Report.nrofReports = 1  
Report.report1 = DTN2Reporter  
DTN2.configFile = cla.confDTN2  
```  
  
## 连接配置文件  
------------------------------------   
DTN2连接配置文件定义ONE中的哪些节点可以连接到哪个DTN2路由器实例。  
它还定义了节点匹配的EID'。  
  
配置文件由具有以下格式的＃和配置行开始的注释行组成：  
`<nodeID> <EID regexp> <dtnd host> <ECL port> <console port>`  
  
这些字段具有以下含义：  
```  
nodeID:		The ID of a node inside ONE (integer >= 0)  
EID regexp:	Incoming bundles whose destination EID matches this regexp  
		will be forwarded to the node inside ONE.  
		(see java.util.regex.Pattern)  
dtnd host:	Hostname/IP of the dtnd router to connect to this node.  
ECL port:	dtnd router's port listening to ECLAs  
console port:	dtnd router's console port  
```  
举例: `# <nodeID> <EID regexp> <dtnd host> <ECL port> <console port>`  
1. `dtn://local-1.dtn/(.*) localhost 8801 5051`  
2. `dtn://local-2.dtn/(.*) localhost 8802 5052`  
  
## Known Issues  
------------  
  
对于DTN2连接相关的问题，你可以联系teemuk@netlab.tkk.fi  
 - 连接到ONE的dtnd路由器实例将导致一个退出.  
   
 # Toolkit  
--------  
  
 该模拟包包括一个名为“toolkit”的文件夹，其中包含用于生成输入和处理的脚本输出模拟器。  
 所有（目前包含的）脚本都是用Perl(http://www.perl.com/)编写的，因此您需要在运行脚本之前安装它。  
   
 一些后处理脚本使用gnuplot(http://www.gnuplot.info/)来创建图形。  
 对于大多数Unix / Linux和Windows环境，这两个程序都可以免费使用。对于Windows环境，可能需要将路径更改为某些脚本的可执行文件.  
   
 `getStats.pl`  
 此脚本可用于创建`MessageStatsReport -report`模块收集的各种统计信息的条形图。  
 唯一的强制性选项是 `-stat`，用于定义应该从报告文件中解析的统计值的名称（例如，`delivery_prob`用于消息传递概率）。  
 其余参数应为MessageStatsReport输出文件名（或paTHS）。  
 脚本创建三个输出文件：一个具有来自所有文件的值，一个具有用于创建图形的gnuplot命令，最后是包含图形的图像文件,为每个输入文件创建一个条形图。  
 使用`-label`选项定义的正则表达式，从报表文件名中解析每个栏的标题。  
 使用`-help`选项运行`getStats.pl`以获取更多help.  
   
 `ccdfPlotter.pl`  
 这个脚本用于从包含time-hitcount-tuple的报表创建Complementary（`/Inverse`）累积分布函数图（使用gluplot）。输出文件名必须使用`-out`选项进行定义  
 其余的参数应该是（合适的）报告文件名。  
 可以使用`-label`选项定义标签提取正则表达式（类似于一个用于getStats脚本），用于仿真的legend.  
   
 `createCreates.pl`  
 消息创建可以使用外部事件文件定义。  
 这样的文件可以使用任何文本编辑器简单地创建，但是这个脚本可以更容易地创建大量的消息。  
 强制选项是消息数（`-nrof`），时间范围（`-time`），终端地址范围（`-hosts`）和消息大小范围（`-sizes`）。  
 消息的数量只是一个整数，但是它们之间带有一个冒号（`:`)的两个整数。  
 如果终端应该回复他们收到的消息，则可以使用`-rsizes`选项定义回复消息的大小范围。  
 如果应该使用某个随机数生成器种子，可以使用`-seed`选项定义。所有随机值都是从包含最小值和独占最大值的均匀分布绘制出来的。  
 脚本输出适合外部事件文件内容的命令。您可能想要将输出重定向到一些file.  
   
 `dtnsim2parser.pl`和`transimsParser.pl`  
 这两个（相当实验的）解析器将数据从其他程序转换为适合于ONE的表单。  
 两者都有两个参数：输入和输出文件。  
 如果省略这些参数，则使用stdin和stdout进行输入和输出。  
 使用`-h`选项，输出简单的帮助文本 `dtnsim2parser`将 dtnsim2(http://watwire.uwaterloo.ca/DTN/sim/) 的输出文件转换为外部事件文件供ONE使用。  
 你可以先通过ONE和ConnectivityDtnsim2Report生成模式关联文件，然后反馈到dtnsim2，最后使用ONE查看结果（使用`dtnsim2parser.pl转换`）  
  
transimsParser可以将TRANSIM(http://transims-opensource.net/)的车辆快照文件转换成移动方式文件供ONE使用  
具体内容查看 `ExternalMovement` 和 `ExternalMovementReader` 类  
