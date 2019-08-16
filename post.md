# Where to put JSON Web Tokens in 2019

A few years ago, I gave a talk about JSON Web Tokens (JWTs) during a Meetup for Java enthusiasts in Eindhoven.
Triggered by a talk about JWTs I attended recently, I decided to dust of my presentation and the demo applications I made back then to see whether they still hold up.
It turns out that life is a little harder in 2019 than it was in 2016, at least as far as security and JWTs are concerned.
Before we go into the details of this opinion, we should first discuss the basics.

## JSON Web Tokens

Essentially, a JSON Web Token is something that a server application would give to a client application, which the client would then use to authenticate itself with the server when doing requests.
A JSON Web Token will look something like this:

**<span style="color: red">eyJhbGciOiJIUzUxMiJ9</span>.<span style="color: fuchsia">eyJleHAiOjE0NzYyOTAxNDksInN1YiI6IjEifQ</span>.<span style="color: blue">mvJEWu3kxm0WSUKu-qEVTBmuelM-2Te-VJHEFclVt_uR89ya0hNawkrgftQbAd-28lycLX2jXCgOGrA3XRg9Jg</span>**

If you look closely, you'll see that it consists of three base64-encoded strings, joined by periods.
If you decode the ones above, you end up with the following:

```
{
  "alg": "HS512"
}
```

```
{
  "exp": 1476290149,
  "sub":"1"
}
```

```
HMACSHA256(
  base64UrlEncode(header) + "." +
  base64UrlEncode(payload),
  secret
)
```

The first part is the header, the second is the payload, and the third is the signature.
Anyone that gets their hands on this token can decode the strings.
(Execute `atob("eyJhbGciOiJIUzUxMiJ9")` in the console of your browser if you want to see for yourself.)
This means that anyone who gets their hands on the token can use the encoded information.
Because only the server knows the secret that was used to compute the signature from the header and body, however, only the server can check the validity of a token by recomputing its expected signature and comparing it with the actual signature.
Once the server has determined that a given JWT is valid, it knows that it issued the token itself, and that the data in the body can be trusted.

## A trip down memory lane

When I first read about JWTs, I was still used to working in an environment where deployments lead to downtime and were something that you'd do very early in the morning, so that they would impact as little end users as possible.
Because they had to take place early in the morning, they didn't occur very frequently.
As a consequence, multiple features where collected and released together, and deployments automatically became stressful.

The back-end applications I worked on at that time maintained sessions for logged in users.
If one of the servers went down, the users whose sessions were stored on that server would lose their session.
In situations like that, you can't just release a bug fix in the middle of the day, because you'd potentially log out part of your users.

First and foremost, I saw JWTs as a solution to this problem.
(There are other, potentially better, solutions to this problem, but let's ignore those for the time being.)
Two or more instances of the same back-end application could sit behind a load balancer and issue JWTs to clients.
All of these instances would be able to validate JWTs issued by any one of them.
The body of each JWT could contain the information that would normally be stored in a session, such as the identifier of the currently logged-in user.
If one of the instance would go down (during a deployment, for example) the load balancer would just route requests to the remaining instance(s) and clients wouldn't notice anything.

I was convinced that JWTs could solve one of my problems, but I wasn't sure how clients and servers should exchange them.
Should they be sent along with requests in a header or should they be kept in a cookie?
In the case of communication between back-end applications, the answer is clear.
It's much easier to follow conventions and put them in a header, and there's no benefit to putting them in cookies instead.
In the case of communication between client applications running in a browser and back-end applications, the answer is less clear.
To assess the pros and cons of both approaches, we need to take a detour.

## CSRF and XSS

The term cross-site request forgery (CSRF) is used for the situation where someone else's web application secretly lets its visitors perform actions with your web application due to cookies still present from previous visits.

The following example (a modified version of one provided by [OWASP](https://www.owasp.org/index.php/Cross-Site_Request_Forgery_(CSRF))) shows a form that tricks unsuspecting users into sending 10.000 euro (?) to my bank account at bank.com:

```
<form action="http://bank.com/transfer.do" method="POST">
  <input type="hidden" name="account" value="LUC"/>
  <input type="hidden" name="amount" value="100000"/>
  <input type="submit" value="View my pictures"/>
</form>
```

The term cross-site scripting (XSS) is used for the situation where someone is able to have their scripts executed as part of your web application.

The following example (directly stolen from [OWASP](https://www.owasp.org/index.php/Cross-site_Scripting_(XSS)) without any extra effort) shows part of a JSP template that allows anyone to execute code on the corresponding web page:

```
<% String eid = request.getParameter("eid"); %>
	...
	Employee ID: <%= eid %>
```

Imagine the nightmares you'll have after clicking http://example.com/employee.jsp?eid=alert%28%22you%20have%20been%20p0wned%22%29...

## Cookie or header?

If you put your JWTs in a cookie, you need to take precautions to combat CSRF.
If you use secure, HTTP-only cookies, you don't need to worry about XSS, however, because scripts don't have access to the content of such cookies.
There's no way someone can abuse XSS and take your JWT to impersonate you.

If you put your JWTs in a header, you don't need to worry about CSRF.
You do need to worry about XSS, however.
If someone can abuse XSS to steal your JWT, this person is able to impersonate you.

In my 2016 presentation, I stated that "defense against CSRF is straightforward and durable."
This statement was based on advice offered by the [Open Web Application Security Project](https://www.owasp.org/) at that time.
Years later, defense against CSRF is still durable, but a little less straightforward.
We'll come back to that in a minute.

XSS, on the other hand, is something you need to constantly keep in mind.
Each template you add could open up possibilities for XSS.
The same holds for all those NPM packages you add to your front-end project, either directly or indirectly.

My conclusion from this is that JWTs belong in a secure, HTTP-only cookie, and should be used in combination with preventive measures against CSRF.

## Seeing is believing

Because the proof of the pudding is in the eating, I wrote a simple front-end app and two back-end apps that demonstrate a session-based and JWT-based approach to authentication: https://github.com/ljpengelen/java-meetup-jwt

With a simple `docker-compose` command, you can start three instances of either of the two back ends, a database, and an instance of nginx that serves the front end and acts as load balancer.
You can open the front end in your browser, create an account, log in, and then stop some of the back-end instances with `docker stop`.

In the case of the JWT-based back end, it doesn't matter which two instances you stop.
In the case of the session-based back end, stopping the instance your connected to will terminate your session.

These applications combat CSRF by checking the `Origin` and `Referer` headers, and by requiring that each request contains a valid CSRF token.
Keeping track of the CSRF tokens in the front end is not completely straightforward, but it's an acceptable price to pay if you ask me.

## Lifespan of a JWT

What happens to already issued JWTs when you change your credentials?
What happens to already issued JWTs when you delete your account?
In both scenarios, existing JWTs will remain valid.
Without additional measures, JWTs remain valid until they expire or until the secret on the server is changed.
If someone gets their hands on a token, it can be abused until it expires.

When should a JWT expire?
They should expire as soon as possible, to prevent misuse for long periods.
On the other hand, they should expire as late as possible, so that users don't have to re-authenticate all the time.

In practice, two types of tokens are used together, to achieve the best of both worlds.
A short-lived *access* token is used for authentication per request.
A long-lived *refresh* token is used to generate new access token when needed.

Each time the refresh token is used to obtain a new access token, some additional checks could be made to enhance security.
The refresh token can be used in combination with a blacklist, for example, to invalidate tokens that were issued for a particular user before a given point in time.

## Conclusion

I am not an expert on this subject, and I must stress that you shouldn't mistake my advice for the absolute truth on this subject.
Instead, I hope this post allows you to follow my reasoning and helps you make informed decisions when you have to choose between different forms of authentication.
