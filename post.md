# Where to put JSON Web Tokens in 2019

A few years ago, I gave a talk about JSON Web Tokens (JWTs) during a Meetup for Java enthusiasts in Eindhoven.
Triggered by a talk about JWTs I attended recently, I decided to dust of my presentation and the demo applications I made back then to see whether they still hold up.
It turns out that life is a little harder in 2019 than it was in 2016, at least as far as security and JWTs are concerned.
Before we go into the details of this opinion, we should first discuss the basics.

## JSON Web Tokens

A JSON Web Token will look something like this:

**<span style="color: red">eyJhbGciOiJIUzUxMiJ9</span>.<span style="color: fuchsia">eyJleHAiOjE0NzYyOTAxNDksInN1YiI6IjEifQ</span>.<span style="color: blue">mvJEWu3kxm0WSUKu-qEVTBmuelM-2Te-VJHEFclVt_uR89ya0hNawkrgftQbAd-28lycLX2jXCgOGrA3XRg9Jg</span>**

If you look closely, you'll see that these are three base64-encoded strings, joined by periods.
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




So, where do you put JWTs in 2019 if you want to use them to authenticate requests from your front end to your back end?
Should you send them along in a header or in cookie?


https://github.com/ljpengelen/java-meetup-jwt
