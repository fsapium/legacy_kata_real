# Lift pass pricing


This application solves the problem of calculating the pricing for ski lift passes.
There's some intricate logic linked to what kind of lift pass you want, your age
and the specific date at which you'd like to ski. There's a new feature request,
be able to get the price for several lift passes, not just one. Currently the pricing
for a single lift pass is implemented, unfortunately the code as it is designed
is ***not reusable***.
You could put some high level tests in place in order to do ***preparatory refactoring***
so that the new feature requires minimum effort to implement.

This kata models a common problem - code that makes no sense to unit test due to bad design.


## When am I done?

There are a few steps, you could do any of them.

1. Cover with high level tests.
1. Refactor the code to maximize unit testability and reuse for the new feature
1. Pull down most of the high level tests
1. Implement the new feature using unit tests and 1 or 2 high level tests.

## Installation

Set up a MySQL database on localhost 3306 with user `root` and password `mysql`.
If you have Docker installed the easiest thing is to use this script, that will initialize a [MariaDB](https://mariadb.org/).

    ci/ -> docker compose up -d

## Tips

There's a good chance you could find a design that is both easier to test, faster to
work with and that solves the problem with minimum amount of code. One such design
would be to rid the bulk of the logic from it's adherence to the http/rest framework
and from the sql specificities. This is sometimes called **hexagonal architecture**
and it facilitates respecting the ***Testing Pyramid*** which is not currently
possible - there can be only top-level tests