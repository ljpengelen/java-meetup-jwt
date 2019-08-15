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

## Seeing is believing

Because the proof of the pudding is in the eating, I wrote a simple front-end app and two back-end apps that demonstrate a session-based and JWT-based approach to authentication: https://github.com/ljpengelen/java-meetup-jwt

With a simple `docker-compose` command you can start three instances of either of the two back ends, a database, and an instance of nginx that serves the front end and acts as load balancer.
You can open the front end in your browser, create an account, log in, and then stop some of the back-end instances with `docker stop`.

In the case of the JWT-based back end, it doesn't matter which two instances you stop.
In the case of the session-based back end, stopping the instance your connected to will terminate your session.

## Conclusion

I am not an expert on this subject, and I must stress that you shouldn't mistake my advice for the absolute truth on this subject.
Instead, I hope this post allows you to follow my reasoning and helps you make informed decisions when you have to choose between different forms of authentication.
