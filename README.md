# PCLocator

PCLocator is a tool to analyze presence conditions in variable software systems
written in the programming language C.

A presence condition describes under which circumstances a file or block of code
is included in the compiled product. A naive, but slow solution is enumerating
all possible configurations of the software.

There are better tools available to analyze variability in C. PCLocator
integrates the following tools:

- [TypeChef](https://github.com/ckaestne/TypeChef) and
  [xtc](https://github.com/paulgazz/xtc), variability-aware C parsers
- [FeatureCoPP](https://dl.acm.org/citation.cfm?id=3001876), a tool for physical
  separation of annotated features
- [Kmax](https://github.com/paulgazz/kmax), a variability-aware analyzer
  Kconfig/Kbuild Makefiles

## Getting started

The easiest way to set up PCLocator is to download a prepared JAR distribution
here (*TODO*).

To build from source, additionally to cloning the repository, you need to
download the libraries here (*TODO*) and copy them to the `lib` folder.
Run the project in IntelliJ to generate the output JAR.

## Usage

PCLocator requires Java 1.8. Run it with:

```
java -jar PCLocator.jar <arguments>
```

If you don't provide any arguments, all available options are explained.

### Examples

Extract a single presence condition for the line `<line>` in a C file `<file>`:

```
java -jar PCLocator.jar <file>:<line>
```

Leave off the `<line>` to get a tabular overview of all presence conditions.

Derive the satisfying configuration space from a feature model in the DIMACS format:

```
java -jar PCLocator.jar --configure <dimacs_file> <file>:<line>
```

Only features declared in the DIMACS file are considered when deriving
configurations to prevent compiler-specific macros being added to the
configuration.

To consider build system information in projects which use Kconfig/Kbuild, you
can use Kmax. PCLocator takes a Kmax unit_pc file (including presence conditions
for every directory and file in the project). Have a look at
[kmax-vm](https://github.com/ekuiter/kmax-vm) if you want to generate such a
file. Some files for Busybox and Linux are included in the JAR distribution of
PCLocator.

Run the analysis with

```
java -jar PCLocator.jar --kmax --kmaxfile <unit_pc_file> --projectroot <source_root> <file>:<line>
```

## Manual library setup

Setting up the `lib` folder can be a little involved, because the integrated
tools depend on different versions of the same library.

Here are instructions to reproduce the libraries provided above, if needed:

- Build TypeChef according to its instructions (`sbt assembly`).
- Edit the JAR (e.g., by extracting and re-zipping it) and remove the `xtc` folder.
  This is necessary because the xtc version included by TypeChef is outdated.
- Copy the resulting JAR to `lib/TypeChef.jar`.
- Download xtc (which includes SuperC) and copy the included JAR files
  (`ccl.jar`, `javabdd.jar`, `javacc.jar`, `javancss.jar`, `JFlex.jar`,
  `jhbasic.jar`, `junit.jar`, `rats-runtime.jar`, `rats.jar`, `xtc.jar`) to `lib`.
- From `FeatureCoPP.jar`, extract `choco-solver-4.0.4-with-dependencies.jar` and
  `JCParser.jar` and copy them to `lib`.