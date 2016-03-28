# OkLog3 example app

Usage example of OkLog3. 

Sample app uses Retrofit2 with OkHttp3 to demonstrate how OkLog3 can be used in conjunction to [OkHttp's HttpLoggingInterceptor](https://github.com/square/okhttp/tree/master/okhttp-logging-interceptor).

This app also serves as my testing playground during development.

## Disclaimer

When creating a production-ready app, you should strongly consider architecting your app in a more maintainable way,
for example by using dependency injection (e.g. Dagger) and the MVP pattern. 
This sample app is not using either in an attempt to make it as simple as possible.

You should also keep in mind to not include any logging in your release builds.
