# 单点登录

首先，单点登录的前置只是通过这篇文章来学习。里面包括Http的无状态性，会话机制，登录状态等基础知识点。这里总结主要应对面试，具体实现的单点登录会上传到github<br />单点登录：实质是多系统应用群中登录一个系统，便可在其他所有系统中得到授权而无需再次登录，包括单点登录与单点注销两部分。<br />**1.登录**<br />相比于单系统登录，sso需要一个独立的认证中心，只有认证中心能接受用户的账号密码等安全信息，其他系统不提供登录入口，只接受认证中心的间接授权。间接授权通过令牌实现，sso认证中心验证用户的账号密码准确无误后，创建授权令牌，在接下来的跳转过程中，授权令牌作为参数发送给各个子系统，子系统拿到令牌，即得到了授权，可以借此创建局部会话，局部会话登录方式与单系统登录方式相同。这个过程，即单点登录的原理，具体如下图所示：<br />
<br />![clipboard.png](https://cdn.nlark.com/yuque/0/2019/png/385379/1562380881483-cfee7a31-527a-4cb3-9259-ba96b1901caf.png#align=left&display=inline&height=931&name=clipboard.png&originHeight=931&originWidth=737&size=227441&status=done&width=737)<br />
<br />
<br />对上图中的流程做一个简要描述：<br />1.用户访问系统1的受保护资源，系统1发现用户未登录，跳转至sso认证中心，并将自己的地址作为参数。<br />2.sso认证中心发现用户未登录，将用户引导至登录界面<br />3.用户输入用户密码提交登录申请<br />4.sso认证中心校验用户信息，创建用户与sso认证中心之间的会话，称为全局会话，同时创建授权令牌。<br />5.sso认证中心带着令牌跳转回最初的请求地址（系统1）<br />6.系统1拿到令牌许可，去sso认证中心校验令牌是否有效<br />7.sso认证中心校验令牌，返回有效，实现系统1层面的登录逻辑。<br />8.系统1使用该令牌创建与用户的会话，称为局部会话，返回受保护资源。<br />9.用户访问系统2的受保护资源。<br />10.系统2发现用户未登录，跳转至sso认证中心，并将自己的地址作为参数。<br />11.sso认证中心发现用户已经登录，跳转回系统2的地址，并附上令牌。<br />12.系统2拿到令牌，去sso认证中心校验令牌是否有效<br />13.sso认证中心校验令牌，返回有效，实现系统2层面的登录逻辑。<br />14.系统2使用该令牌创建与用户的局部会话，返回受保护的资源。<br />
<br />**用户登录成功之后，会与sso认证中心及各个子系统建立会话，用户与sso认证中心建立的会话为全局会话，用户与各个子系统建立的会话称为局部会话，局部会话建立之后，用户访问子系统受保护资源将不再通过sso认证中心，全局会话与局部会话有如下约束：**<br />1.局部会话存在，则全局会话一定存在<br />2.全局会话存在，局部会话不一定存在<br />3.全局会话销毁，局部会话必须销毁。<br />**2.注销**<br />单点登录自然也要单点注销，在一个子系统中注销，所有子系统的会话都将被销毁，用下图来说明<br />![clipboard.png](https://cdn.nlark.com/yuque/0/2019/png/385379/1562380930392-3e9303d9-2b34-470e-88ca-0fe3a2564bf0.png#align=left&display=inline&height=499&name=clipboard.png&originHeight=499&originWidth=698&size=130282&status=done&width=698)<br />sso认证中心一直监听全局会话的状态，一旦全局会话销毁，监听器将通知所有注册系统执行注销操作。<br />上图中的简要逻辑如下：<br />1.用户向系统1发起注销请求<br />2.系统1根据用户与系统1建立的会话id拿到令牌，向sso认证中心发起注销请求<br />3.sso认证中心校验令牌有效，销毁全局会话，同时取出所有用此令牌注册的系统地址。<br />4.sso认证中心向所有注册系统发起注销请求<br />5.各注册系统接收sso认证中心的注销请求，销毁局部会话<br />6,sso认证中心引导用户至登录页面。<br />**部署图**<br />**单点登录涉及sso认证中心与众子系统，**子系统与sso认证中心需要通信以交换令牌，校验令牌及发起注销请求，因而子系统必须继承sso的客户端，sso认证中心则是sso服务端，整个单点登录过程实质是sso客户端与服务端通信的过程。<br />![clipboard (1).png](https://cdn.nlark.com/yuque/0/2019/png/385379/1562380941845-6d87b53f-4b3f-45a0-b76b-033690381ea1.png#align=left&display=inline&height=544&name=clipboard%20%281%29.png&originHeight=544&originWidth=707&size=178761&status=done&width=707)<br />
<br />sso认证中心与sso客户端通信方式有很多种，参考博客，我将会使用httpClient。当然web service，rpc，restful api都可以。<br />总结一下sso的优点吧：<br />1.提升用户体验，一处登录，多处使用<br />2.避免重复开发。<br />3.提升安全系数。<br />实现SSO的几种方法：<br />1.最经典的Cookie+sessio方法。<br />以同域场景为基础简单介绍一下：<br />两个产品系统在同一个域名下时，具体步骤如下

1. 用户访问系统a，向后台服务器发送登录请求。
1. 登录认证成功，服务器把用户的登录信息写入sessio
1. 服务器为该用户生成一个cookie，并加入到response header中，随着请求返回而写入浏览器。设置cookie的域设定为dxy.cn
1. 下一次，当用户访问系统b的时候，由于a，b在同一个域名之下，所以，浏览器会自动带上之前的cookie，此时后台服务器可以通过cookie来验证登录状态。

![clipboard (2).png](https://cdn.nlark.com/yuque/0/2019/png/385379/1562380996604-0d1bdf5c-74ca-4018-9fb3-9bddbbc590cb.png#align=left&display=inline&height=952&name=clipboard%20%282%29.png&originHeight=952&originWidth=906&size=162619&status=done&width=906)<br />这是最传统的登录方式。<br />同样的还存在同父域登录的场景。即a系统对应域名为vip.dxy.cn。b系统对应的域名为normal.dxy.cn。此时将cookie的domain设置为其父域即可（dxy.cn）<br />在访问a和b时，这个cookie都能发送到服务器，本质上和同域sso的区别在于domain的设计。<br />2.CAS方法，这个我也是刚接触，简单学习总结一下。<br />这里的CAS与并发中CAS方法不是一个概念。SSO是一种架构，一种设计，而CAS是实现SSO的一种手段。两者都是抽象与具体的关系。CAS能够很好地解决跨域问题，不过这貌似是前端的问题。我也只是听过而已。<br />相关概念：

- TGT: Ticket Gragtig Ticket

TGT 是CAS为用户签发的登录票据，拥有了TGT，用户可以证明自己在CAS成功登录过。TGT封装了Cookie值以及此Cookie值对应的用户信息。当HTTP请求到来时，CAS以此Cookie值（TGC）为key查询缓存中有无TGT。如果有的话，则系统判定用户已经登录过。

- TGC: Ticket Granting Cookie

CAS Server 生成TGT放入自己的Session中，而TGC作为该Session的唯一标识。以Cookie形式放到浏览器端，是CAS Server用来明确用户身份的凭证。

- ST: Service Ticket

ST是CAS为用户签发的访问某一service的票据，用户访问service时，service发现用户没有ST。则要求用户去CAS获取ST。用户向CAS发出获取ST请求，CAS发现用户有TGT，则签发一个ST，发返回给用户。用户拿着ST去访问service，service拿ST去CAS签证。验证通过之后，用户可以访问service。<br />**详细步骤如下：**

1. 用户访问系统a,域名[www.a.cn](http://www.a.cn)
1. 由于用户没有携带在a服务器上登录的a cookie，所以a服务器将用户请求重定向到sso服务器上，同时url的query中通过参数指明登录成功后，回跳到a页面。重定向的url类似于如下形式：

 sso.dxy.cn/login?service=https%3A%2F%2Fwww.a.cn

3. 由于用户没有携带在sso服务器上登录的TGC，所以SSO服务器判断用户未登录，给用户显示统一登录界面。用户在sso的页面上进行登录操作。
3. 登录成功之后，sso服务器构建用户的TGT，同时返回一个http重定向。这里需要注意：
- 重定向地址为之前写在query内的a页面
- 重定向地址的query中包含sso服务器派发的ST
- 重定向的http response中包含写cookie的header，这个cookie代表用户在sso中的登录状态，它的值就是TGC。

5.浏览器重定向到产品a，此时重定向的url中携带着SSO服务器生成的ST<br />6.根据ST，a服务器向SSO服务器发送请求，SSO服务器验证票据的有效性。验证成功之后，a服务器直到用户已经在sso登录，于是a服务器构建用户登录session，记为a session。并将cookie写入浏览器。注意，此处的cookie和session保存的是用户在a服务器的登录状态，和cas无关。<br />7.同样的访问系统b，域名是[www.b](http://www.b).cn<br />8.由于用户没有携带在 b 服务器上登录的 b cookie，所以 b 服务器返回 http 重定向，重定向的 url 是 SSO 服务器的地址，去询问用户在 SSO 中的登录状态。<br />9.浏览器重定向到sso，因为之前已经向浏览器写入了携带TGC的cookie（第4步），所以此时sso服务器可以能拿到，根据TGC查找TGT，如果找到了，就判断用户已经在sso登录过。<br />10.SSO服务器返回一个重定向，重定向携带ST，注意，这里的ST和之前的ST是不一样的。且每次生成的ST都是不一样的。<br />11.浏览器带ST重定向到b服务器，与第5步一样。<br />12.b服务器根据票据向SSO服务器发送请求，票据验证通过之后，b服务器直到用户已经在sso登录过，于是生成b session，向浏览器写入b cookie。<br />![clipboard (3).png](https://cdn.nlark.com/yuque/0/2019/png/385379/1562381030690-66bcf255-d472-474f-acaf-ab20251d4e99.png#align=left&display=inline&height=1026&name=clipboard%20%283%29.png&originHeight=1026&originWidth=914&size=368350&status=done&width=914)

整个流程结束了，之后当用户访问a或b之后，直接回携带a cookie/b cookie 不需要再像sso确认了。
