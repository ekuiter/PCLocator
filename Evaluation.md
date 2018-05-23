# Evaluation

These are runtime measurements for deriving a single configuration including the
given program location. The default (merge) parser was used, meaning that all
tools (TypeChef, SuperC and FeatureCoPP) have been used and the best presence
condition was selected.

```
splc18challengecase/vbdb/marlin/simple/2d22902.c:11 	 0m1.727s
splc18challengecase/vbdb/marlin/simple/7336e6d.c:44 	 0m1.926s
splc18challengecase/vbdb/marlin/simple/7336e6d.c:45 	 0m2.058s
splc18challengecase/vbdb/marlin/simple/831016b.c:16 	 0m1.343s
splc18challengecase/vbdb/marlin/simple/8c4377d.c:16 	 0m1.743s
splc18challengecase/vbdb/marlin/simple/b8e79dc.c:27 	 0m1.864s
splc18challengecase/vbdb/marlin/simple/fdac8f6.c:9 	 0m1.627s
splc18challengecase/vbdb/busybox/simple/199501f.c:22 	 0m2.289s
splc18challengecase/vbdb/busybox/simple/1b487ea.c:15 	 0m1.913s
splc18challengecase/vbdb/busybox/simple/2631486.c:42 	 0m1.906s
splc18challengecase/vbdb/busybox/simple/5275b1e.c:25 	 0m2.038s
splc18challengecase/vbdb/busybox/simple/5cd6461.c:23 	 0m2.066s
splc18challengecase/vbdb/busybox/simple/9575518.c:48 	 0m2.316s
splc18challengecase/vbdb/busybox/simple/b7ebc61.c:17 	 0m1.696s
splc18challengecase/vbdb/busybox/simple/bc0ffc0.c:44 	 0m2.000s
splc18challengecase/vbdb/busybox/simple/cf1f2ac.c:29 	 0m1.841s
splc18challengecase/vbdb/busybox/simple/df7b657.c:8 	 0m1.672s
splc18challengecase/vbdb/busybox/simple/ebee301.c:24 	 0m1.813s
splc18challengecase/vbdb/linux/simple/0988c4c.c:16 	 0m1.772s
splc18challengecase/vbdb/linux/simple/0dc77b6.c:38 	 0m1.820s
splc18challengecase/vbdb/linux/simple/0f8f809.c:39 	 0m2.130s
splc18challengecase/vbdb/linux/simple/1f758a4.c:40 	 0m1.593s
splc18challengecase/vbdb/linux/simple/208d898.c:32 	 0m1.939s
splc18challengecase/vbdb/linux/simple/221ac32.c:37 	 0m2.274s
splc18challengecase/vbdb/linux/simple/242f1a3.c:18 	 0m1.463s
splc18challengecase/vbdb/linux/simple/2f02c15.c:18 	 0m1.487s
splc18challengecase/vbdb/linux/simple/472a474.c:22 	 0m1.576s
splc18challengecase/vbdb/linux/simple/51fd36f.c:44 	 0m2.054s
splc18challengecase/vbdb/linux/simple/60e233a.c:27 	 0m2.029s
splc18challengecase/vbdb/linux/simple/6252547.c:13 	 0m1.538s
splc18challengecase/vbdb/linux/simple/63878ac.c:17 	 0m1.712s
splc18challengecase/vbdb/linux/simple/6515e48.c:11 	 0m1.475s
splc18challengecase/vbdb/linux/simple/657e964.c:69 	 0m1.848s
splc18challengecase/vbdb/linux/simple/6651791.c:17 	 0m1.522s
splc18challengecase/vbdb/linux/simple/6651791.c:25 	 0m1.515s
splc18challengecase/vbdb/linux/simple/6e2b757.c:66 	 0m1.884s
splc18challengecase/vbdb/linux/simple/76baeeb.c:40 	 0m2.194s
splc18challengecase/vbdb/linux/simple/76baeeb.c:45 	 0m2.126s
splc18challengecase/vbdb/linux/simple/7c6048b.c:24 	 0m1.499s
splc18challengecase/vbdb/linux/simple/8c82962.c:17 	 0m2.150s
splc18challengecase/vbdb/linux/simple/91ea820.c:40 	 0m2.152s
splc18challengecase/vbdb/linux/simple/ae249b5.c:20 	 0m2.129s
splc18challengecase/vbdb/linux/simple/bc8cec0.c:28 	 0m1.705s
splc18challengecase/vbdb/linux/simple/c708c57.c:21 	 0m1.987s
splc18challengecase/vbdb/linux/simple/d530db0.c:19 	 0m1.852s
splc18challengecase/vbdb/linux/simple/d549f55.c:14 	 0m1.982s
splc18challengecase/vbdb/linux/simple/d6c7e11.c:20 	 0m1.607s
splc18challengecase/vbdb/linux/simple/d7e9711.c:11 	 0m2.097s
splc18challengecase/vbdb/linux/simple/e1fbd92.c:17 	 0m1.926s
splc18challengecase/vbdb/linux/simple/e67bc51.c:12 	 0m1.471s
splc18challengecase/vbdb/linux/simple/e68bb91.c:10 	 0m1.457s
splc18challengecase/vbdb/linux/simple/eb91f1d.c:36 	 0m2.159s
splc18challengecase/vbdb/linux/simple/ee3f34e.c:46 	 0m2.044s
splc18challengecase/vbdb/linux/simple/f3d83e2.c:14 	 0m2.020s
splc18challengecase/vbdb/linux/simple/f48ec1d.c:10 	 0m1.490s
splc18challengecase/vbdb/linux/simple/f7ab9b4.c:55 	 0m1.792s
```

That is 103.308s in total for 56 program locations, 1.84s average, 0.25s
standard deviation.

The results were observed on a quad-core 2,3 GHz CPU with 12 GB RAM using macOS.
