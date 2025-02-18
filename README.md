What is RESOLVE?
==============
[![RESOLVE (Custom Java CI with Maven)](https://github.com/rhit-csse-projects/RESOLVE/actions/workflows/maven.yml/badge.svg)](https://github.com/rhit-csse-projects/RESOLVE/actions/workflows/maven.yml)
[![License](https://img.shields.io/badge/license-BSD-blue.svg)](https://raw.githubusercontent.com/ClemsonRSRG/RESOLVE/master/LICENSE.txt)
<img align="right" src="src/resources/images/resolve_logo.png" width="200"/>

RESOLVE (REusable SOftware Language with VErification) is a specification and programming language designed for verifying correctness of object oriented programs.

The RESOLVE language is designed from the ground up to facilitate *mathematical reasoning*. As such, the language provides syntactic slots for assertions such as pre-post conditions that are capable of abstractly describing a program's intended behavior. In writing these assertions, users are free to draw from from a variety of pre-existing and user-defined mathematical theories containing fundamental axioms, definitions, and results necessary/useful in establishing program correctness.

All phases of the verification process spanning verification condition (VC) generation to proving are performed in-house, while RESOLVE programs are translated to Java and run on the JVM.

## Authors and major contributors

The creation and continual evolution of the RESOLVE language is owed to an ongoing joint effort between Clemson University, The Ohio State University, and countless educators and researchers from a variety of [other](https://www.cs.clemson.edu/resolve/about.html) institutions.

Developers of this particular test/working-iteration of the `RESOLVE Compiler` include:

* [RESOLVE Software Research Group (RSRG)](https://www.cs.clemson.edu/resolve/) - School of Computing, Clemson University

## Copyright and license

Copyright © 2024, [RESOLVE Software Research Group (RSRG)](https://www.cs.clemson.edu/resolve/). All rights reserved. The use and distribution terms for this software are covered by the BSD 3-clause license which can be found in the file `LICENSE.txt` at the root of this repository. By using this software in any fashion, you are agreeing to be bound by the terms of this license. You must not remove this notice, or any other, from this software.
