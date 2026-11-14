# Hello and Welcome to my First Full stack Project: Class Organizer

## 📋 Table of Contents

1. 🎯[The Problem](#the-problem-i-wanted-to-solve)
2. 🚀 [Running the Program](#running-the-program)
3. [Running the Tests](#running-the-tests)
4. [Using MySQL](#using-mysql)
5. 📦[Deployment](#deployment)
6. 💻 [The Tech](#the-tech)
7. [What changed in 2026, and why](#what-changed-in-2026-and-why)
8. 🎨[Design](#design)
9. 📬[Contact](#contact)
10. [Licence](#licence)

## <a name="the-problem-i-wanted-to-solve">🎯 The Problem</a>

For my first web app project, I aimed to address a common issue among my college classmates. The class schedule we received was not organized or visualized in a way that provided a clear understanding of how our days looked like. I set out to create a simple program that allows users to create an account, record their class data, and visualize a clearer schedule.

## <a name="running-the-program">🚀 Running the Program</a>

All you need is Java 17 or newer. From the project folder, run one command:

```
./mvnw jetty:run
```

Then open http://localhost:8080/ (on Windows the command is `mvnw.cmd jetty:run`). Stop it with Ctrl+C.

The Maven wrapper (`mvnw`) downloads the right Maven by itself, and Maven downloads everything else. Jetty, a small Java web server started through its official Maven plugin, serves the same servlets and JSP pages GlassFish did. I picked Jetty because its plugin runs the app straight from the source folders, JSP pages included, so one command builds and starts everything with nothing to install.

This way of running uses an embedded database: H2 in MySQL mode, kept in memory and built from `src/main/resources/schema.sql`. It needs no database server and starts empty every time. To use a real MySQL instead, see [Using MySQL](#using-mysql).

## <a name="running-the-tests">Running the Tests</a>

```
./mvnw verify
```

This builds the app and runs the tests:

- **Passwords:** the hashing on its own (`PasswordsTest`).
- **The running app:** Maven starts the app on port 18089 and the tests use it over HTTP, like a browser. One walks through every page (sign up, log in, add a class, see the schedule, remove a class, account info, log out), and the rest check each fault the 2023 version had (`FlowIT`, `SecurityIT`, `FormsIT`).

GitHub runs the same command on every push and pull request (`.github/workflows/build.yml`).

## <a name="using-mysql">Using MySQL</a>

MySQL is still the real database. The app reads the same three environment variables it always has:

| Variable | What it holds | Example |
| --- | --- | --- |
| `SQL_Connection` | The JDBC URL of the database | `jdbc:mysql://localhost:3306/classorganizer` |
| `user` | The database user | `classorganizer` |
| `password` | That user's password | (yours) |

When `SQL_Connection` is set, it wins over the embedded database. To run a MySQL on your own computer, `compose.yaml` starts one with Docker and loads `schema.sql` the first time:

```
export MYSQL_PASSWORD=pick-a-password
docker compose up -d
SQL_Connection=jdbc:mysql://localhost:3306/classorganizer user=classorganizer password="$MYSQL_PASSWORD" ./mvnw jetty:run
```

If you use a MySQL you set up yourself (Amazon RDS, for example), run `src/main/resources/schema.sql` against it once first. A database from the 2023 version will not work: the password column changed, so make a fresh database for this version. Old accounts cannot be carried over either, because their passwords were never hashed, so everyone signs up again.

Note: I built and tested this update against the embedded database. The MySQL route above is written and checked by reading, but I have not run it against a live MySQL yet.

## <a name="deployment">📦 Deployment</a>

After building the application, you are provided with a WAR file that can be used to deploy the project. I deployed this project on Amazon Web Services using an EC2 instance in the same VPC as my RDS. My EC2 was in a public subnet, and my RDS remained in a private subnet for secure data storage. I used SSH protocol to transfer the WAR file to my EC2 instance.

Today `./mvnw package` still builds that WAR in `target/`. It runs on any Jakarta EE 10 server, such as GlassFish 7, with `SQL_Connection`, `user` and `password` set on the server.

## <a name="the-tech">💻 The Tech</a>

- **Frontend:** HTML, CSS, JSP ![HTML Badge](https://img.shields.io/badge/-HTML-orange?style=flat&logo=html5)
  ![CSS Badge](https://img.shields.io/badge/-CSS-blue?style=flat&logo=css3)
  ![JSP Badge](https://img.shields.io/badge/-JSP-green?style=flat)

- **Backend:** Java 17, Servlet API (Jakarta Servlet 6.0) ![Java Badge](https://img.shields.io/badge/-Java-red?style=flat&logo=java)

- **Database:** MySQL, with H2 in MySQL mode for local runs and tests ![MySQL Badge](https://img.shields.io/badge/-MySQL-blue?style=flat&logo=mysql)

- **Server:** Jetty 12 for local runs, GlassFish 7.0 or any Jakarta EE 10 server for the WAR ![GlassFish Badge](https://img.shields.io/badge/-GlassFish-yellow?style=flat)

- **Build and tests:** Maven, JUnit

- Object-oriented programming approach

## <a name="what-changed-in-2026-and-why">What changed in 2026, and why</a>

The app looks and works the same as in 2023. These are the five changes I learned the most from.

- **Passwords.** In 2023 I saved passwords exactly as people typed them. Now they are saved as salted PBKDF2 hashes (`Passwords.java`), which the JDK can make with no extra library. If the table ever leaks, the passwords do not.
- **Logging in.** My old login looked the account up by its password, so two students with the same password could land in each other's accounts. Now it finds the account by username and then checks the password. Logging in also starts a new session, and logging out ends it.
- **Queries.** Some of my queries glued typed text straight into the SQL, so a username could rewrite the query, and connections were never closed. Now every query is a `PreparedStatement` with `?` parameters inside try-with-resources, which is Java's version of C#'s `using`.
- **Each student's own classes.** Remove Class used to delete a CRN for every student who had it, not just for the one logged in. Now every query on classes carries the student's id, so one student can never see or remove another's classes.
- **Checking and escaping input.** My forms trusted whatever came in, a blank number crashed the page, and names went onto the page as raw HTML. Now `Validation.java` checks every field on the server and shows a plain message, and everything a person typed is escaped before it is shown.

# <a name="design">🎨 Design</a>

## <a name="home">Home</a>
![Local Image](Home.png)

## <a name="create-account">Create Account</a>
![Local Image](CreateAccount.png)

## <a name="login">Login</a>
![Local Image](Login.png)

## <a name="landing">Landing</a>
![Local Image](Landing.png)

## <a name="add-class">Add Class</a>
![Local Image](AddClass.png)

## <a name="remove-class">Remove Class</a>
![Local Image](RemoveClass.png)

## <a name="schedule">Schedule</a>
![Local Image](Schedule.png)

## <a name="account-info">Account Info</a>
![Local Image](AccountInfo.png)

## <a name="contact">📬 Contact</a>
[![LinkedIn](https://img.shields.io/badge/-LinkedIn-0A66C2?style=flat&logo=linkedin&logoColor=white)](https://www.linkedin.com/in/tomy-romero-902476145/)
Any questions, comments, or concerns, email me at tomyfletcher99@hotmail.com. Hope you guys enjoy.

## <a name="licence">Licence</a>

The code is under the MIT licence: see [LICENSE](LICENSE).

The libraries it uses keep their own licences, and none of them is committed here; Maven downloads them when it builds. The MySQL driver (MySQL Connector/J), which goes into the WAR, is under the GNU GPL version 2 with the Universal FOSS Exception, which allows using it with an open source project under MIT. The driver also brings Google's Protocol Buffers library (protobuf-java) into the WAR, under the BSD 3-Clause licence. The embedded H2 database, also in the WAR, is under the MPL 2.0 or the EPL 1.0. Jetty, JUnit and the Servlet API are only used to build, run and test the app and are not shipped with it.
