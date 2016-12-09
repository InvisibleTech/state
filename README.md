# State Finder REST Service
## Requirements and Building
* Java 8, code was built with and tested on `1.8.0_74`.
* Maven, version used `3.3.9`
* Build commend: `mvn clean install`
* It will build a self-contained executable jar, but needs network access to get dependencies.

## Running
Use the shell script `./state` after building and it should launch service.  Then use REST client such as `curl` or `postman`.

## Motivation for Using Java
* Lack of strong background in Python, so wanted to deal with app and domain not the lanuage.
* Scala may have been too annoying for reviewers.
* Not strong enough in Elixir yet to have used Phoenix and it seems to be the concensus that Elixir is not for crunching.

## Most Time Spent In
* Quickly getting up to speed on the dataset given and any concerns in how to handle it.
* Thinking about approach and finding an algorithm I liked for the problem after looking for open source libraries for Java.  Most libraries are huge and add abstractions for which I would have used only one or two functions.  https://github.com/Esri/geometry-api-java looked most interesting.
* Finding a light weight Java framework for REST.  Most are too heavy.  Spark, not Big Data Spark, seems to be a nice, lean choice with a functional/lambda based model.
