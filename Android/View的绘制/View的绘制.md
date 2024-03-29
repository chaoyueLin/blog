从Android 4.1（版本代号为Jelly Bean）开始，Android OS开发团队便力图在每个版本中解决一个重要问题（这是不是也意味着Android OS在经过几轮大规模改善后，开始进入手术刀式的精加工阶段呢？）。作为严重影响Android口碑问题之一的UI流畅性差的问题，首先在Android 4.1版本中得到了有效处理。其解决方法就是本文要介绍的Project Butter。
Project Butter对Android Display系统进行了重构，引入了三个核心元素，即VSYNC、Triple Buffer和Choreographer。其中，VSYNC是理解Project Buffer的核心。VSYNC是Vertical Synchronization（垂直同步）的缩写，是一种在PC上已经很早就广泛使用的技术。读者可简单的把它认为是一种定时中断。
接下来，本文将围绕VSYNC来介绍Android Display系统的工作方式[①]。请注意，后续讨论将以Display为基准，将其划分成16ms长度的时间段，在每一时间段中，Display显示一帧数据（相当于每秒60帧）。时间段从1开始编号。
首先是没有VSYNC的情况，如图1所示：

![](./0749b2b9-d67d-4346-bf0f-4eeca19d86c7.png)
由图1可知：时间从0开始，进入第一个16ms：Display显示第0帧，CPU处理完第一帧后，GPU紧接其后处理继续第一帧。三者互不干扰，一切正常。
时间进入第二个16ms：因为早在上一个16ms时间内，第1帧已经由CPU，GPU处理完毕。故Display可以直接显示第1帧。显示没有问题。但在本16ms期间，CPU和GPU却并未及时去绘制第2帧数据（注意前面的空白区），而是在本周期快结束时，CPU/GPU才去处理第2帧数据。
时间进入第3个16ms，此时Display应该显示第2帧数据，但由于CPU和GPU还没有处理完第2帧数据，故Display只能继续显示第一帧的数据，结果使得第1帧多画了一次（对应时间段上标注了一个Jank）。
通过上述分析可知，此处发生Jank的关键问题在于，为何第1个16ms段内，CPU/GPU没有及时处理第2帧数据？原因很简单，CPU可能是在忙别的事情（比如某个应用通过sleep固定时间来实现动画的逐帧显示），不知道该到处理UI绘制的时间了。可CPU一旦想起来要去处理第2帧数据，时间又错过了！
为解决这个问题，Project Buffer引入了VSYNC，这类似于时钟中断。结果如图2所示：

![](./c5a9f361-2468-401a-98c6-af0c69300e1c.png)

由图2可知，每收到VSYNC中断，CPU就开始处理各帧数据。整个过程非常完美。
不过，仔细琢磨图2却会发现一个新问题：图2中，CPU和GPU处理数据的速度似乎都能在16ms内完成，而且还有时间空余，也就是说，CPU/GPU的FPS（帧率，Frames Per Second）要高于Display的FPS。确实如此。由于CPU/GPU只在收到VSYNC时才开始数据处理，故它们的FPS被拉低到与Display的FPS相同。但这种处理并没有什么问题，因为Android设备的Display FPS一般是60，其对应的显示效果非常平滑。
如果CPU/GPU的FPS小于Display的FPS，会是什么情况呢？请看图3：

![](./23dbdb46-015e-4249-a53b-4dfa7d631650.png)

由图3可知：在第二个16ms时间段，Display本应显示B帧，但却因为GPU还在处理B帧，导致A帧被重复显示。
同理，在第二个16ms时间段内，CPU无所事事，因为A Buffer被Display在使用。B Buffer被GPU在使用。注意，一旦过了VSYNC时间点，CPU就不能被触发以处理绘制工作了。
为什么CPU不能在第二个16ms处开始绘制工作呢？原因就是只有两个Buffer。如果有第三个Buffer的存在，CPU就能直接使用它，而不至于空闲。出于这一思路就引出了Triple Buffer。结果如图4所示：

![](./4de6c2fb-8c62-4da8-a591-fda73cbc365b.png)

由图4可知：
第二个16ms时间段，CPU使用C Buffer绘图。虽然还是会多显示A帧一次，但后续显示就比较顺畅了。
是不是Buffer越多越好呢？回答是否定的。由图4可知，在第二个时间段内，CPU绘制的第C帧数据要到第四个16ms才能显示，这比双Buffer情况多了16ms延迟。所以，Buffer最好还是两个，三个足矣。
介绍了上述背景知识后，下文将分析Android Project Buffer的一些细节。
## Project Buffer分析
上一节对VSYNC进行了理论分析，其实也引出了Project Buffer的三个关键点：
核心关键：需要VSYNC定时中断。
Triple Buffer：当双Buffer不够使用时，该系统可分配第三块Buffer。
另外，还有一个非常隐秘的关键点：即将绘制工作都统一到VSYNC时间点上。这就是Choreographer的作用。Choreographer是一个极富诗意的词，意为舞蹈编导。在它的统一指挥下，应用的绘制工作都将变得井井有条。
