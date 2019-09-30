# mrJar.wizard

Build A JPMS Modular RESTful Authentication Application with Jersey, Dropwizard and [*the **mrJar** Gradle Plugin*](https://bit.ly/mrJar)

## Building Modular Java Applications with the ***mrJar*** Gradle Plugin

This project demonstrates the ease with which you can build and run a relatively complex module-based application with the help of [*the **mrJar** plugin*](http://bit.ly/mrJar.com). It was ported from [*an already existing project*](http://bit.ly/akudRepo) that uses a different Java module plugin.

### Prerequisites

• MySQL database <br />
• Java 9<sup>+</sup> <br />
• Gradle 5.0<sup>+</sup> <br />
• A *`REST`* client (*e.g., `curl`*) <br />
• A *`DATABASE_JDBC_URL`* environment variable <br />
• An *`AUTHENTICATION_URL`* environment variable <br /> 

The original repo on which this repo is based has [*a companion blog*](http://bit.ly/akudBlog). The author of that blog, did an outstanding job explaining in detail all the steps needed to get a Gradle build of his original Java 9<sup>+</sup> module project executing successfully. But that was the blog's main focus. Building the project. Frustratingly, it provided no instructions on how to actually ***use*** the modules after they were built nor how to actually ***run*** the application made up of the modules. 

If you are like me and have never used Dropwizard, then that blog's lack of ***usage*** instructions made an already difficult subject (*JPMS modules*) that much more difficult!

### mrJar — JPMS Modules Made Easy

The plugin that was used in that blog's original code has been replaced by ***mrJar*** in this demo repo. That change alone made this demo project's *`build.gradle`* itself a lot simpler. Using ***mrJar*** also makes the usage of JPMS modules a lot easier. The instructions in this README explain how to:

• Customize the MySQL database configuration for your environment <br />
• Edit this project's *`build.gradle`* to build and run the application and client modules <br />
• Build the JPMS modules in this repo <br />
• Initialize the database with a *`users*` table <br />
• Start the embedded server that hosts the *`REST`*-ful endpoints implemented by this project <br />
• Run the application and client modules <br />

### MySQL database configuration

You will need a MySQL database. And you will need to configure Dropwizard to use that database. Like the original, this project uses an *`authentication_application.yml`* file to configure Dropwizard. Included in that file is a *`database`* section with a *`url: ${DATABASE_JDBC_URL}`* line. You are, therefore, required to set an environment variable named *`DATABASE_JDBC_URL`* with a value that is the *`URL`* of your MySQL database. For example my Windows machine's name is *`lingocoder`*. And the MySQL database that I created for this demo is named *`mrjar`*; it runs on MySQL's default port. So *`set DATABASE_JDBC_URL=jdbc:mysql://lingocoder/mrjar`* worked for me.

This project contains another *`yaml`* file named *`migrations.yml`*. Dropwizard uses that file to, among other things, initialize your MySQL database. I have a very old MySQL version 3.23.36 that I've had on my machine for a coon's age. I haven't used it in years. But it works fine. And I'm frankly too lazy to upgrade it. So although the *`migrations.yml`* file in this repo has been customized to be usable in older versions of MySQL, it should work fine on newer versions too. Just in case, I've also included *`migrations.original.yml`* alongside it as a fallback.

### Refactor *`build.gradle`* to use ***mrJar***

The following refactoring has been implemented to simplify the *`build.gradle`* and streamline the building and running of this project's JPMS modules:

1. Replace the *`com.zyxist.chainsaw`* plugin with the ***mrJar*** plugin
2. Remove the *`com.github.johnrengelman.shadow`* plugin and its associated configuration
3. Move the build logic originally contained in three standalone sub-project *`build.gradle`* scripts to be inside the root project's *`build.gradle`* <br />
   • this is a way to impose a strict ordering on which sub-projects are built in relation to others <br />
   • because the *authentication-application* project has what Gradle calls a *lib* dependency on the *authentication-models* project, this arrangment will ensure that the one is built before the other <br />
   • this arrangement eliminates the need for the *only-runs-on-\*nix-* *`build.sh`* script that the original project imposed <br />
   • three fewer files to maintain makes the build that much simpler overall <br />
4. Add a *`mrjar{ }`* block to the *authentication-client* section of the root project's composite *`build.gradle`*:
    
        mrjar{ 
            main = 'com.alexkudlick.authentication.client.AuthenticationClientApplication'
            args = ['--create', 'mister.jar', , 'testPassword']
        }
    
5. Add a *`mrjar{ }`* block to the *authentication-application* section of the root project's composite *`build.gradle`*:
    
        mrjar{ 
            main = 'com.alexkudlick.authentication.application.AuthenticationApplication'
            args = ['db', 'migrate', 'authentication_application.yml']
        }
        
### Build the JPMS modules

This is the easiest, most straightforward part of the whole process. 

1. First, do a sanity check to make sure everything is good-to-go:

       gradlew check
       ...
       BUILD SUCCESSFUL
       ...

2. Then bring 'er on home:

       gradlew assemble
       ...
       BUILD SUCCESSFUL
       ...

At this point you have three modular jars in the three sub-projects' respective *`build/libs`* folders. You can confirm ***mrJar's*** successful creation of either of the three explicit modules with:

        jar -f authentication-application/build/libs/authentication-application.jar --describe-module
        com.alexkudlick.authentication.application jar:file:///.../authentication-application/build/libs/authentication-application.jar/!module-info.class
        requires com.alexkudlick.authentication.models
        requires com.fasterxml.jackson.databind
        requires com.google.common
        requires dropwizard.configuration
        requires dropwizard.core
        ...
        qualified opens com.alexkudlick.authentication.application.entities to hibernate.core javassist
        qualified opens com.alexkudlick.authentication.application.web to jersey.server

### Start the server

Make sure you've set the *`DATABASE_JDBC_URL`* environment variable. The authentication-application's build script needs to look like this:

    mrjar{ 
        main = 'com.alexkudlick.authentication.application.AuthenticationApplication'
        args = ['server', 'authentication_application.yml']
    }
    
Then you're good to run:

    gradlew authentication-application:run

### Initialize the database

The *`REST`* resources this project focuses on are *Tokens* and *Users*. So before you can do anything with the application, there needs to be a *User* table in the database. Before we run the command to create that table, you have to first set a *`DATABASE_JDBC_URL`*. For example on *nix you could do:

    export DATABASE_JDBC_URL=jdbc:mysql://lingocoder/mrjar

Now, using the *authentication-application* module assembled earlier, together with the *`db migrate...`* args configured in the *`mrjar{ }`* block of its build script...
    
    mrjar{ 
        main = 'com.alexkudlick.authentication.application.AuthenticationApplication'
        args = ['db', 'migrate', 'authentication_application.yml']
    }
        

 ...then executing this command in the directory of the root project will create a *`User`* table in your db:


    gradlew authentication-application:run

    ...
    liquibase: migrations.yml: migrations.yml::1547851554156-1::alex (generated): Table hibernate_sequence created
    ...
    liquibase: migrations.yml: migrations.yml::1547851554156-2::alex (generated): Table users created
    ...

Once you have a *`User`* table, you are almost ready to run the *`authentication-client`* module to create a *`User`* using the *`--create`* properties you configured in the *`mrjar{ }`* block of the *`authentication-client`*  build script above. First make sure your build script looks like what I described above:

    
    mrjar{ 
        main = 'com.alexkudlick.authentication.client.AuthenticationClientApplication'
        args = ['--create', 'mister.jar', , 'testPassword']
    }
    

Also, you need an *`AUTHENTICATION_URL`* environment variable set to *`http://localhost:8080`*.  Then, from the root directory, run this command to create a user:

    gradlew authentication-client:run

    ...   
    [system.out] ok
    [org.gradle.process.internal.DefaultExecHandle] Changing state to: SUCCEEDED


If you run the client module without an *`args`* property in the *`mrjar{ }`* block, then you will be shown the *`usage`* screen:
    
    [system.out] usage: 
    [system.out]        --create userName password
    [system.out]        --login userName password
    [system.out]        --check token

To run the *`check`* command, you will need to pass it the token that was created when you created the *`User`*. To retrieve a *`User`* token for the one you just created, using the build script example above, comment out the *`--create`* line and uncomment the *`--login`* line: 

    mrjar{
        main = 'com.alexkudlick.authentication.client.AuthenticationClientApplication'
        args = ['--login','mister.jar', 'testPassword']
    }

    gradlew application-client:run
    ...
    [system.out] 1e0bdd68-b591-4c9a-9755-cb326b04ad3d
    [org.gradle.process.internal.DefaultExecHandle] Changing state to: SUCCEEDED
    ...
You could also fire this message to the server to get the same result:

    $ curl -i -X POST -H "Content-Type: application/json" --data '{"userName": "mister.jar", "password": "testpassword"}' http://localhost:8080/api/tokens/

    ...

    {"token":"1e0bdd68-b591-4c9a-9755-cb326b04ad3d"}

The authentication-application will also show you its available commands if you run it without setting an *`args`* property in its *`mrjar{ }`* block:

    mrjar{ 
        main = 'com.alexkudlick.authentication.client.AuthenticationClientApplication'
    }

    usage: java -jar project.jar [-h] [-v] {server,check,db} ...

    positional arguments:
      {server,check,db}      available commands

    optional arguments:
    -h, --help             show this help message and exit
    -v, --version          show the application version and exit

    ...
    BUILD SUCCESSFUL in 29s
    ...

Try out the *`db status`* command:

    [class org.gradle.internal.buildevents.TaskExecutionLogger] > Task :authentication-application:run
    ...
    [ system.out] DEBUG [2019-09-23 20:21:08,757] liquibase: migrations.yml: migrations.yml::1547851554156-1::alex (generated): Computed checksum for createTable:[
    [ system.out]     columns=[
    [ system.out]         [
    [ system.out]             name="next_val"
    [ system.out]             type="BIGINT(19)"
    [ system.out]         ]
    [ system.out]     ]
    [ system.out]     tableName="hibernate_sequence"
    [ system.out] ] as 061d12ec8fe9d28b32a0364e3579520d
    ...
    [org.gradle.process.internal.DefaultExecHandle] Changing state to: SUCCEEDED
   
This demonstration showed you how ***mrJar*** makes it easy to organize, build and run an application architected with JPMS modules. If this page does not go into sufficient technology detail for you, then you might find [*the original blog*](http://bit.ly/akudBlog) of some value.

Please get in touch with any questions.
