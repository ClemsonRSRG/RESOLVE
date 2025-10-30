What is RESOLVE?
==============
[![RESOLVE (Custom Java CI with Maven)](https://github.com/rhit-csse-projects/RESOLVE/actions/workflows/maven.yml/badge.svg)](https://github.com/rhit-csse-projects/RESOLVE/actions/workflows/maven.yml)
[![License](https://img.shields.io/badge/license-BSD-blue.svg)](https://raw.githubusercontent.com/ClemsonRSRG/RESOLVE/master/LICENSE.txt)
<img align="right" src="src/resources/images/resolve_logo.png" width="200"/>

RESOLVE (REusable SOftware Language with VErification) is a specification and programming language designed for verifying correctness of object oriented programs.

The RESOLVE language is designed from the ground up to facilitate *mathematical reasoning*. As such, the language provides syntactic slots for assertions such as pre-post conditions that are capable of abstractly describing a program's intended behavior. In writing these assertions, users are free to draw from from a variety of pre-existing and user-defined mathematical theories containing fundamental axioms, definitions, and results necessary/useful in establishing program correctness.

All phases of the verification process spanning verification condition (VC) generation to proving are performed in-house, while RESOLVE programs are translated to Java and run on the JVM.

## Project Goal
This project's goal is to implement a new proving architecture as detailed in the following diagram.
![goal](https://github.com/user-attachments/assets/55136166-625d-429c-90d2-b8f2822bdac0)

## Authors and major contributors

This iteration of the RESOLVE compiler is being developed as a student capstone project at Rose-Hulman Institute of Technology, advised by [Dr. Joseph Hollingsworth](mailto:hollings@rose-hulman.edu). This work is based on the [Clemson Univerity RESOLVE compiler](https://github.com/ClemsonRSRG/RESOLVE).

## Copyright and license

Copyright © 2025, Rose-Hulman Institute of Technology and [RESOLVE Software Research Group (RSRG)](https://www.cs.clemson.edu/resolve/). All rights reserved. The use and distribution terms for this software are covered by the BSD 3-clause license which can be found in the file `LICENSE.txt` at the root of this repository. By using this software in any fashion, you are agreeing to be bound by the terms of this license. You must not remove this notice, or any other, from this software.
