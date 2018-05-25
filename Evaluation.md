# Evaluation

Here are the evaluation results for running PCLocator on the supported target
systems. The results were observed on a quad-core 2,3 GHz CPU with 12 GB RAM
running macOS.

## Variability Bugs Database

These are runtime measurements for deriving a single configuration including the
given program location (`vbdb_challenge_peek.sh`). The default (merge) parser
was used, meaning that all tools (TypeChef, SuperC and FeatureCoPP) have been
used and the best presence condition was selected.

```
0m1.727s    splc18challengecase/vbdb/marlin/simple/2d22902.c:11
0m1.926s    splc18challengecase/vbdb/marlin/simple/7336e6d.c:44
0m2.058s    splc18challengecase/vbdb/marlin/simple/7336e6d.c:45
0m1.343s    splc18challengecase/vbdb/marlin/simple/831016b.c:16
0m1.743s    splc18challengecase/vbdb/marlin/simple/8c4377d.c:16
0m1.864s    splc18challengecase/vbdb/marlin/simple/b8e79dc.c:27
0m1.627s    splc18challengecase/vbdb/marlin/simple/fdac8f6.c:9
0m2.289s    splc18challengecase/vbdb/busybox/simple/199501f.c:22
0m1.913s    splc18challengecase/vbdb/busybox/simple/1b487ea.c:15
0m1.906s    splc18challengecase/vbdb/busybox/simple/2631486.c:42
0m2.038s    splc18challengecase/vbdb/busybox/simple/5275b1e.c:25
0m2.066s    splc18challengecase/vbdb/busybox/simple/5cd6461.c:23
0m2.316s    splc18challengecase/vbdb/busybox/simple/9575518.c:48
0m1.696s    splc18challengecase/vbdb/busybox/simple/b7ebc61.c:17
0m2.000s    splc18challengecase/vbdb/busybox/simple/bc0ffc0.c:44
0m1.841s    splc18challengecase/vbdb/busybox/simple/cf1f2ac.c:29
0m1.672s    splc18challengecase/vbdb/busybox/simple/df7b657.c:8
0m1.813s    splc18challengecase/vbdb/busybox/simple/ebee301.c:24
0m1.772s    splc18challengecase/vbdb/linux/simple/0988c4c.c:16
0m1.820s    splc18challengecase/vbdb/linux/simple/0dc77b6.c:38
0m2.130s    splc18challengecase/vbdb/linux/simple/0f8f809.c:39
0m1.593s    splc18challengecase/vbdb/linux/simple/1f758a4.c:40
0m1.939s    splc18challengecase/vbdb/linux/simple/208d898.c:32
0m2.274s    splc18challengecase/vbdb/linux/simple/221ac32.c:37
0m1.463s    splc18challengecase/vbdb/linux/simple/242f1a3.c:18
0m1.487s    splc18challengecase/vbdb/linux/simple/2f02c15.c:18
0m1.576s    splc18challengecase/vbdb/linux/simple/472a474.c:22
0m2.054s    splc18challengecase/vbdb/linux/simple/51fd36f.c:44
0m2.029s    splc18challengecase/vbdb/linux/simple/60e233a.c:27
0m1.538s    splc18challengecase/vbdb/linux/simple/6252547.c:13
0m1.712s    splc18challengecase/vbdb/linux/simple/63878ac.c:17
0m1.475s    splc18challengecase/vbdb/linux/simple/6515e48.c:11
0m1.848s    splc18challengecase/vbdb/linux/simple/657e964.c:69
0m1.522s    splc18challengecase/vbdb/linux/simple/6651791.c:17
0m1.515s    splc18challengecase/vbdb/linux/simple/6651791.c:25
0m1.884s    splc18challengecase/vbdb/linux/simple/6e2b757.c:66
0m2.194s    splc18challengecase/vbdb/linux/simple/76baeeb.c:40
0m2.126s    splc18challengecase/vbdb/linux/simple/76baeeb.c:45
0m1.499s    splc18challengecase/vbdb/linux/simple/7c6048b.c:24
0m2.150s    splc18challengecase/vbdb/linux/simple/8c82962.c:17
0m2.152s    splc18challengecase/vbdb/linux/simple/91ea820.c:40
0m2.129s    splc18challengecase/vbdb/linux/simple/ae249b5.c:20
0m1.705s    splc18challengecase/vbdb/linux/simple/bc8cec0.c:28
0m1.987s    splc18challengecase/vbdb/linux/simple/c708c57.c:21
0m1.852s    splc18challengecase/vbdb/linux/simple/d530db0.c:19
0m1.982s    splc18challengecase/vbdb/linux/simple/d549f55.c:14
0m1.607s    splc18challengecase/vbdb/linux/simple/d6c7e11.c:20
0m2.097s    splc18challengecase/vbdb/linux/simple/d7e9711.c:11
0m1.926s    splc18challengecase/vbdb/linux/simple/e1fbd92.c:17
0m1.471s    splc18challengecase/vbdb/linux/simple/e67bc51.c:12
0m1.457s    splc18challengecase/vbdb/linux/simple/e68bb91.c:10
0m2.159s    splc18challengecase/vbdb/linux/simple/eb91f1d.c:36
0m2.044s    splc18challengecase/vbdb/linux/simple/ee3f34e.c:46
0m2.020s    splc18challengecase/vbdb/linux/simple/f3d83e2.c:14
0m1.490s    splc18challengecase/vbdb/linux/simple/f48ec1d.c:10
0m1.792s    splc18challengecase/vbdb/linux/simple/f7ab9b4.c:55
```

That is 103.308s in total for 56 program locations, 1.84s average, 0.25s
standard deviation.

## BusyBox

This is an overview for our results on 661 random program locations
(`busybox_random.sh`). Like with VBDB, a single configuration was derived for
each location.

**Merge parser**

| metric        | time            |
| ------------- | --------------- |
| total         | 116.917 minutes |
| mean          | 10.613s         |
| deviation     | 17.45s          |
| min           | 0.392s          |
| max           | 5.677 minutes   |

**SuperC**

| metric        | time            |
| ------------- | --------------- |
| total         | 27.37 minutes   |
| mean          | 2.48s           |
| deviation     | 1.92s           |
| min           | 0.38s           |
| max           | 31.51s          |

**FeatureCoPP**

| metric        | time            |
| ------------- | --------------- |
| total         | 14.14 minutes   |
| mean          | 1.28s           |
| deviation     | 0.08s           |
| min           | 0.39s           |
| max           | 1.76s           |

Below are the detailed results. The columns show the merge, SuperC and
FeatureCoPP parsers respectively.

```
0m14.221s   0m2.584s  0m1.317s  busybox-1.18.5/libbb/print_flags.c:24
0m48.850s   0m2.826s  0m1.342s  busybox-1.18.5/modutils/lsmod.c:82
0m6.813s    0m1.491s  0m1.558s  busybox-1.18.5/coreutils/stty.c:567
0m7.121s    0m2.975s  0m1.342s  busybox-1.18.5/networking/ftpgetput.c:68
0m7.609s    0m2.957s  0m1.254s  busybox-1.18.5/shell/match.c:12
0m13.796s   0m1.668s  0m1.254s  busybox-1.18.5/archival/libarchive/lzo1x_1.c:14
0m13.214s   0m2.359s  0m1.218s  busybox-1.18.5/libbb/herror_msg.c:17
0m8.172s    0m4.368s  0m1.335s  busybox-1.18.5/procps/ps.c:556
0m15.122s   0m3.229s  0m1.257s  busybox-1.18.5/networking/libiproute/rt_names.c:131
0m19.213s   0m2.803s  0m1.277s  busybox-1.18.5/modutils/modutils.c:180
0m43.475s   0m2.772s  0m1.244s  busybox-1.18.5/modutils/lsmod.c:49
0m6.403s    0m1.367s  0m1.316s  busybox-1.18.5/selinux/setfiles.c:150
0m7.077s    0m2.873s  0m1.262s  busybox-1.18.5/sysklogd/logread.c:25
0m6.473s    0m1.358s  0m1.313s  busybox-1.18.5/coreutils/expr.c:49
0m5.974s    0m1.334s  0m1.248s  busybox-1.18.5/libbb/find_root_device.c:36
0m6.741s    0m2.511s  0m1.224s  busybox-1.18.5/selinux/matchpathcon.c:66
0m7.240s    0m1.379s  0m1.377s  busybox-1.18.5/archival/libarchive/decompress_unzip.c:802
0m13.310s   0m2.309s  0m1.252s  busybox-1.18.5/libbb/get_volsize.c:5
0m3.007s    0m1.022s  0m1.245s  busybox-1.18.5/e2fsprogs/old_e2fsprogs/uuid/pack.c:33
0m15.309s   0m1.401s  0m1.262s  busybox-1.18.5/networking/nc.c:66
0m3.118s    0m0.977s  0m1.242s  busybox-1.18.5/e2fsprogs/old_e2fsprogs/ext2fs/flushb.c:29
0m8.461s    0m4.732s  0m1.311s  busybox-1.18.5/networking/ifplugd.c:398
0m7.020s    0m2.371s  0m1.278s  busybox-1.18.5/shell/cttyhack.c:141
0m7.371s    0m2.199s  0m1.261s  busybox-1.18.5/networking/ipcalc.c:88
0m10.360s   0m8.562s  0m1.408s  busybox-1.18.5/miscutils/hdparm.c:1453
0m13.833s   0m2.406s  0m1.218s  busybox-1.18.5/libbb/perror_msg.c:39
0m3.008s    0m0.959s  0m1.286s  busybox-1.18.5/e2fsprogs/old_e2fsprogs/ext2fs/icount.c:67
0m7.014s    0m2.748s  0m1.246s  busybox-1.18.5/coreutils/split.c:49
0m7.184s    0m1.495s  0m1.446s  busybox-1.18.5/archival/dpkg.c:1107
0m13.202s   0m2.318s  0m1.219s  busybox-1.18.5/libbb/setup_environment.c:17
0m2.312s    0m0.964s  0m1.239s  busybox-1.18.5/libbb/xatonum_template.c:50
0m3.016s    0m0.984s  0m1.251s  busybox-1.18.5/e2fsprogs/old_e2fsprogs/ext2fs/alloc.c:50
0m13.171s   0m2.782s  0m1.244s  busybox-1.18.5/loginutils/add-remove-shell.c:70
0m7.126s    0m2.955s  0m1.246s  busybox-1.18.5/miscutils/microcom.c:142
0m6.994s    0m2.652s  0m1.251s  busybox-1.18.5/miscutils/beep.c:51
0m2.598s    0m1.594s  0m1.202s  busybox-1.18.5/shell/ash_test/zecho.c:2
0m2.607s    0m1.626s  0m1.228s  busybox-1.18.5/shell/ash_test/printenv.c:22
0m9.925s    0m1.518s  0m1.257s  busybox-1.18.5/e2fsprogs/old_e2fsprogs/util.c:145
0m2.968s    0m0.970s  0m1.270s  busybox-1.18.5/e2fsprogs/old_e2fsprogs/ext2fs/dblist.c:228
0m15.956s   0m1.515s  0m1.322s  busybox-1.18.5/archival/libarchive/unxz/xz_dec_bcj.c:394
0m7.339s    0m3.618s  0m1.262s  busybox-1.18.5/coreutils/tr.c:316
0m3.239s    0m1.650s  0m1.243s  busybox-1.18.5/miscutils/bbconfig.c:5
0m13.684s   0m2.153s  0m1.211s  busybox-1.18.5/libbb/bb_basename.c:17
0m7.164s    0m3.229s  0m1.277s  busybox-1.18.5/procps/sysctl.c:47
0m12.974s   0m1.349s  0m1.222s  busybox-1.18.5/archival/libarchive/get_header_tar_bz2.c:4
0m12.834s   0m2.371s  0m1.218s  busybox-1.18.5/debianutils/mktemp.c:45
0m12.407s   0m1.508s  0m1.314s  busybox-1.18.5/shell/shell_common.c:85
0m22.045s   0m2.157s  0m1.256s  busybox-1.18.5/networking/libiproute/ll_proto.c:45
0m13.541s   0m2.529s  0m1.220s  busybox-1.18.5/modutils/insmod.c:32
0m6.954s    0m2.344s  0m1.228s  busybox-1.18.5/util-linux/volume_id/jfs.c:47
0m6.393s    0m1.310s  0m1.270s  busybox-1.18.5/procps/pgrep.c:30
0m3.437s    0m1.110s  0m1.320s  busybox-1.18.5/e2fsprogs/old_e2fsprogs/uuid/uuid_time.c:36
0m14.533s   0m1.427s  0m1.357s  busybox-1.18.5/archival/libarchive/filter_accept_list.c:7
0m6.932s    0m2.767s  0m1.343s  busybox-1.18.5/util-linux/hexdump.c:141
0m2.915s    0m0.946s  0m1.313s  busybox-1.18.5/e2fsprogs/old_e2fsprogs/blkid/tag.c:203
0m7.427s    0m3.403s  0m1.328s  busybox-1.18.5/miscutils/last_fancy.c:151
0m7.302s    0m3.724s  0m1.401s  busybox-1.18.5/coreutils/dd.c:217
0m3.153s    0m0.944s  0m1.321s  busybox-1.18.5/e2fsprogs/old_e2fsprogs/blkid/resolve.c:119
0m3.012s    0m1.038s  0m1.330s  busybox-1.18.5/e2fsprogs/old_e2fsprogs/ext2fs/bb_inode.c:41
0m6.456s    0m1.530s  0m1.199s  busybox-1.18.5/e2fsprogs/old_e2fsprogs/e2p/uuid.c:58
0m6.859s    0m2.672s  0m1.246s  busybox-1.18.5/coreutils/sum.c:66
0m7.988s    0m4.546s  0m1.362s  busybox-1.18.5/loginutils/getty.c:47
0m13.325s   0m2.285s  0m1.222s  busybox-1.18.5/libbb/device_open.c:16
0m7.459s    0m3.203s  0m1.299s  busybox-1.18.5/networking/ether-wake.c:232
0m7.032s    0m2.863s  0m1.261s  busybox-1.18.5/loginutils/addgroup.c:18
0m5.325s    0m4.853s  0m1.368s  busybox-1.18.5/libbb/appletlib.c:349
0m6.489s    0m1.401s  0m1.238s  busybox-1.18.5/e2fsprogs/old_e2fsprogs/e2p/mntopts.c:86
0m8.212s    0m4.930s  0m1.333s  busybox-1.18.5/runit/runsv.c:349
0m13.501s   0m3.004s  0m1.230s  busybox-1.18.5/libbb/make_directory.c:30
0m7.469s    0m3.690s  0m1.312s  busybox-1.18.5/util-linux/mkfs_reiser.c:202
0m8.376s    0m4.402s  0m1.330s  busybox-1.18.5/networking/ifplugd.c:538
0m14.029s   0m2.204s  0m1.227s  busybox-1.18.5/coreutils/true.c:5
0m6.808s    0m2.288s  0m1.264s  busybox-1.18.5/shell/cttyhack.c:115
0m7.084s    0m2.707s  0m1.350s  busybox-1.18.5/debianutils/run_parts.c:113
0m16.427s   0m1.479s  0m1.486s  busybox-1.18.5/networking/udhcp/dhcpd.c:221
0m14.143s   0m2.330s  0m1.351s  busybox-1.18.5/miscutils/makedevs.c:118
0m13.500s   0m2.102s  0m1.269s  busybox-1.18.5/coreutils/true.c:17
0m13.254s   0m2.141s  0m1.256s  busybox-1.18.5/libbb/xgethostbyname.c:17
0m13.046s   0m2.277s  0m1.254s  busybox-1.18.5/libbb/full_write.c:32
0m13.896s   0m2.199s  0m1.265s  busybox-1.18.5/coreutils/chgrp.c:21
0m13.865s   0m2.319s  0m1.252s  busybox-1.18.5/procps/uptime.c:57
0m9.349s    0m4.837s  0m1.397s  busybox-1.18.5/util-linux/mkfs_minix.c:215
0m8.575s    0m2.510s  0m1.297s  busybox-1.18.5/miscutils/ubi_attach_detach.c:38
0m13.658s   0m2.666s  0m1.266s  busybox-1.18.5/libbb/correct_password.c:51
0m2.993s    0m0.973s  0m1.264s  busybox-1.18.5/e2fsprogs/old_e2fsprogs/ext2fs/cmp_bitmaps.c:27
0m13.897s   0m1.377s  0m1.224s  busybox-1.18.5/archival/libarchive/header_verbose_list.c:56
0m2.324s    0m0.883s  0m1.215s  busybox-1.18.5/util-linux/fdisk_aix.c:47
0m7.374s    0m3.762s  0m1.295s  busybox-1.18.5/mailutils/mime.c:3
0m13.139s   0m2.857s  0m1.239s  busybox-1.18.5/loginutils/cryptpw.c:82
0m17.363s   0m1.485s  0m1.331s  busybox-1.18.5/networking/udhcp/dhcpd.c:19
0m20.512s   0m10.908s 0m1.448s  busybox-1.18.5/networking/ntpd.c:849
0m7.063s    0m3.052s  0m1.261s  busybox-1.18.5/loginutils/adduser.c:193
0m3.463s    0m1.051s  0m1.254s  busybox-1.18.5/e2fsprogs/old_e2fsprogs/ext2fs/finddev.c:162
0m13.234s   0m2.192s  0m1.204s  busybox-1.18.5/procps/uptime.c:3
0m7.077s    0m3.286s  0m1.252s  busybox-1.18.5/printutils/lpd.c:170
0m13.809s   0m1.413s  0m1.205s  busybox-1.18.5/archival/libarchive/filter_accept_all.c:7
0m6.150s    0m1.355s  0m1.268s  busybox-1.18.5/coreutils/du.c:87
0m3.067s    0m0.965s  0m1.339s  busybox-1.18.5/e2fsprogs/old_e2fsprogs/ext2fs/inode.c:122
0m7.114s    0m3.008s  0m1.375s  busybox-1.18.5/printutils/lpr.c:142
0m18.803s   0m1.314s  0m1.325s  busybox-1.18.5/networking/udhcp/dumpleases.c:77
0m3.267s    0m0.944s  0m1.307s  busybox-1.18.5/e2fsprogs/old_e2fsprogs/ext2fs/initialize.c:193
0m3.417s    0m1.893s  0m1.241s  busybox-1.18.5/libbb/mode_string.c:107
0m13.056s   0m1.384s  0m1.256s  busybox-1.18.5/miscutils/taskset.c:123
0m9.110s    0m6.304s  0m1.373s  busybox-1.18.5/procps/top.c:109
0m6.165s    0m1.379s  0m1.263s  busybox-1.18.5/miscutils/flash_eraseall.c:62
0m17.740s   0m1.412s  0m1.214s  busybox-1.18.5/networking/udhcp/dumpleases.c:34
0m2.581s    0m1.043s  0m1.322s  busybox-1.18.5/networking/nc_bloaty.c:877
0m6.858s    0m2.548s  0m1.230s  busybox-1.18.5/util-linux/lspci.c:41
0m13.182s   0m2.373s  0m1.214s  busybox-1.18.5/util-linux/setarch.c:20
0m13.421s   0m2.325s  0m1.224s  busybox-1.18.5/coreutils/fsync.c:21
0m12.517s   0m2.451s  0m1.210s  busybox-1.18.5/libbb/read.c:27
0m7.232s    0m1.422s  0m1.298s  busybox-1.18.5/archival/libarchive/decompress_unlzma.c:229
0m3.613s    0m1.131s  0m1.267s  busybox-1.18.5/e2fsprogs/old_e2fsprogs/ext2fs/getsize.c:15
0m7.082s    0m2.972s  0m1.263s  busybox-1.18.5/networking/vconfig.c:14
0m7.436s    0m3.485s  0m1.272s  busybox-1.18.5/networking/brctl.c:105
0m12.931s   0m2.392s  0m1.272s  busybox-1.18.5/libbb/xgetcwd.c:14
0m3.663s    0m1.739s  0m1.242s  busybox-1.18.5/libbb/perror_nomsg_and_die.c:21
0m6.692s    0m2.424s  0m1.273s  busybox-1.18.5/coreutils/cat.c:8
0m7.971s    0m4.523s  0m1.350s  busybox-1.18.5/networking/zcip.c:526
0m7.212s    0m1.414s  0m1.334s  busybox-1.18.5/archival/bzip2.c:147
0m15.190s   0m1.366s  0m1.356s  busybox-1.18.5/networking/udhcp/packet.c:286
0m13.406s   0m2.307s  0m1.287s  busybox-1.18.5/util-linux/lsusb.c:30
0m23.346s   0m2.504s  0m1.304s  busybox-1.18.5/util-linux/rev.c:17
0m6.875s    0m2.359s  0m1.287s  busybox-1.18.5/util-linux/volume_id/romfs.c:3
0m7.208s    0m2.857s  0m1.312s  busybox-1.18.5/networking/nbd-client.c:11
0m2.946s    0m0.945s  0m1.332s  busybox-1.18.5/e2fsprogs/old_e2fsprogs/ext2fs/bitops.c:53
0m24.481s   0m2.964s  0m1.325s  busybox-1.18.5/coreutils/expand.c:53
0m3.593s    0m1.080s  0m1.365s  busybox-1.18.5/e2fsprogs/old_e2fsprogs/ext2fs/ismounted.c:5
0m3.801s    0m1.675s  0m1.321s  busybox-1.18.5/libbb/makedev.c:19
0m13.363s   0m2.317s  0m1.240s  busybox-1.18.5/coreutils/logname.c:9
0m6.896s    0m2.616s  0m1.342s  busybox-1.18.5/miscutils/mt.c:129
0m14.312s   0m1.804s  0m1.242s  busybox-1.18.5/archival/libarchive/lzo1x_1.c:4
0m12.692s   0m2.240s  0m1.276s  busybox-1.18.5/coreutils/dirname.c:12
0m7.069s    0m2.939s  0m1.341s  busybox-1.18.5/util-linux/volume_id/ntfs.c:129
0m7.481s    0m3.125s  0m1.375s  busybox-1.18.5/networking/ether-wake.c:233
0m13.439s   0m2.283s  0m1.277s  busybox-1.18.5/libbb/parse_config.c:177
0m6.912s    0m2.421s  0m1.235s  busybox-1.18.5/util-linux/volume_id/unused_silicon_raid.c:68
0m6.360s    0m1.311s  0m1.307s  busybox-1.18.5/miscutils/flash_eraseall.c:180
0m22.615s   0m2.745s  0m1.264s  busybox-1.18.5/util-linux/rev.c:22
0m12.992s   0m2.611s  0m1.304s  busybox-1.18.5/libbb/copyfd.c:1
0m2.928s    0m0.957s  0m1.316s  busybox-1.18.5/e2fsprogs/old_e2fsprogs/ext2fs/block.c:81
0m2.855s    0m0.943s  0m1.277s  busybox-1.18.5/e2fsprogs/old_e2fsprogs/ext2fs/sparse.c:1
0m7.921s    0m5.298s  0m1.384s  busybox-1.18.5/procps/powertop.c:851
0m6.306s    0m1.346s  0m1.331s  busybox-1.18.5/miscutils/flash_eraseall.c:47
0m6.893s    0m2.319s  0m1.275s  busybox-1.18.5/miscutils/runlevel.c:15
0m7.317s    0m3.227s  0m1.431s  busybox-1.18.5/util-linux/volume_id/fat.c:46
0m13.123s   0m1.377s  0m1.347s  busybox-1.18.5/archival/libarchive/header_list.c:4
0m14.397s   0m1.334s  0m1.337s  busybox-1.18.5/archival/dpkg_deb.c:1
0m2.477s    0m0.963s  0m1.384s  busybox-1.18.5/util-linux/fdisk_sun.c:166
0m19.461s   0m2.784s  0m1.313s  busybox-1.18.5/modutils/modutils.c:9
0m16.904s   0m3.771s  0m1.309s  busybox-1.18.5/networking/libiproute/libnetlink.c:37
0m3.197s    0m0.980s  0m1.233s  busybox-1.18.5/e2fsprogs/old_e2fsprogs/ext2fs/version.c:35
0m0.392s    0m0.382s  0m0.386s  busybox-1.18.5/modutils/modutils-24.c:398
0m13.412s   0m2.429s  0m1.237s  busybox-1.18.5/coreutils/catv.c:41
0m3.609s    0m1.043s  0m1.280s  busybox-1.18.5/e2fsprogs/old_e2fsprogs/ext2fs/getsize.c:2
0m4.896s    0m1.830s  0m1.385s  busybox-1.18.5/archival/libarchive/unxz/xz_dec_stream.c:751
0m6.773s    0m2.561s  0m1.265s  busybox-1.18.5/libbb/xfunc_die.c:1
0m8.623s    0m5.963s  0m1.371s  busybox-1.18.5/editors/diff.c:509
1m2.573s    0m4.868s  0m1.344s  busybox-1.18.5/networking/libiproute/iptunnel.c:444
0m2.154s    0m1.524s  0m1.217s  busybox-1.18.5/shell/ash_ptr_hack.c:1
0m13.220s   0m2.379s  0m1.209s  busybox-1.18.5/libbb/change_identity.c:27
0m6.635s    0m2.305s  0m1.211s  busybox-1.18.5/libbb/bb_qsort.c:16
0m7.406s    0m3.681s  0m1.307s  busybox-1.18.5/mailutils/mime.c:7
0m2.976s    0m0.959s  0m1.251s  busybox-1.18.5/e2fsprogs/old_e2fsprogs/ext2fs/swapfs.c:15
0m13.470s   0m1.477s  0m1.194s  busybox-1.18.5/archival/libarchive/data_extract_to_stdout.c:5
0m3.389s    0m1.663s  0m1.227s  busybox-1.18.5/miscutils/bbconfig.c:18
0m6.810s    0m2.506s  0m1.231s  busybox-1.18.5/procps/pidof.c:35
0m13.411s   0m2.129s  0m1.150s  busybox-1.18.5/libbb/getpty.c:28
0m6.846s    0m1.727s  0m1.213s  busybox-1.18.5/sysklogd/syslogd_and_logger.c:10
0m13.991s   0m2.939s  0m1.260s  busybox-1.18.5/e2fsprogs/chattr.c:103
0m10.241s   0m8.963s  0m1.423s  busybox-1.18.5/miscutils/hdparm.c:33
0m13.665s   0m3.119s  0m1.249s  busybox-1.18.5/libbb/read_key.c:259
0m6.801s    0m2.474s  0m1.228s  busybox-1.18.5/util-linux/volume_id/xfs.c:26
0m5.907s    0m1.391s  0m1.248s  busybox-1.18.5/libbb/inode_hash.c:40
0m6.781s    0m2.526s  0m1.247s  busybox-1.18.5/util-linux/volume_id/iso9660.c:53
0m9.670s    0m1.407s  0m1.260s  busybox-1.18.5/e2fsprogs/old_e2fsprogs/util.c:115
0m13.288s   0m2.193s  0m1.212s  busybox-1.18.5/libbb/bb_bswap_64.c:2
0m7.107s    0m2.681s  0m1.269s  busybox-1.18.5/loginutils/su.c:12
0m6.919s    0m2.492s  0m1.284s  busybox-1.18.5/util-linux/volume_id/unused_highpoint.c:27
0m3.344s    0m0.951s  0m1.269s  busybox-1.18.5/e2fsprogs/old_e2fsprogs/ext2fs/rs_bitmap.c:87
0m6.750s    0m1.451s  0m1.355s  busybox-1.18.5/networking/telnetd.c:477
0m7.062s    0m1.436s  0m1.370s  busybox-1.18.5/archival/libarchive/decompress_unzip.c:881
0m3.314s    0m0.974s  0m1.261s  busybox-1.18.5/e2fsprogs/old_e2fsprogs/ext2fs/imager.c:272
0m14.984s   0m2.378s  0m1.231s  busybox-1.18.5/libbb/info_msg.c:60
0m6.240s    0m1.381s  0m1.232s  busybox-1.18.5/e2fsprogs/old_e2fsprogs/e2p/feature.c:172
0m2.320s    0m0.875s  0m1.233s  busybox-1.18.5/util-linux/fdisk_aix.c:25
0m8.176s    0m6.014s  0m1.355s  busybox-1.18.5/editors/ed.c:783
0m3.057s    0m0.974s  0m1.264s  busybox-1.18.5/e2fsprogs/old_e2fsprogs/ext2fs/swapfs.c:87
0m12.996s   0m2.477s  0m1.223s  busybox-1.18.5/selinux/getsebool.c:34
0m12.978s   0m2.610s  0m1.251s  busybox-1.18.5/coreutils/catv.c:8
0m17.990s   0m19.031s 0m1.613s  busybox-1.18.5/util-linux/fdisk.c:369
0m13.322s   0m2.369s  0m1.204s  busybox-1.18.5/libbb/isdirectory.c:3
0m3.026s    0m0.996s  0m1.209s  busybox-1.18.5/e2fsprogs/old_e2fsprogs/ext2fs/cmp_bitmaps.c:14
0m13.322s   0m2.309s  0m1.152s  busybox-1.18.5/libbb/getpty.c:28
0m6.786s    0m2.576s  0m1.237s  busybox-1.18.5/util-linux/volume_id/luks.c:80
0m7.225s    0m2.740s  0m1.253s  busybox-1.18.5/util-linux/volume_id/get_devname.c:1
0m13.386s   0m2.520s  0m1.268s  busybox-1.18.5/libbb/llist.c:56
1m6.883s    0m4.658s  0m1.353s  busybox-1.18.5/networking/libiproute/iptunnel.c:216
0m6.242s    0m1.389s  0m1.262s  busybox-1.18.5/miscutils/flash_eraseall.c:173
0m7.363s    0m2.325s  0m1.245s  busybox-1.18.5/init/halt.c:50
0m6.776s    0m1.405s  0m1.328s  busybox-1.18.5/util-linux/ipcs.c:497
0m2.492s    0m0.939s  0m1.261s  busybox-1.18.5/libbb/pw_encrypt_sha.c:130
0m7.996s    0m5.367s  0m1.318s  busybox-1.18.5/libbb/dump.c:30
0m16.819s   0m3.838s  0m1.327s  busybox-1.18.5/networking/tcpudp.c:176
0m14.147s   0m2.811s  0m1.253s  busybox-1.18.5/libbb/uuencode.c:49
0m13.325s   0m2.423s  0m1.213s  busybox-1.18.5/libbb/trim.c:23
0m6.628s    0m1.345s  0m1.275s  busybox-1.18.5/networking/udhcp/dhcprelay.c:69
0m13.512s   0m2.234s  0m1.215s  busybox-1.18.5/selinux/load_policy.c:6
0m13.017s   0m3.240s  0m1.274s  busybox-1.18.5/coreutils/echo.c:82
0m7.388s    0m3.710s  0m1.325s  busybox-1.18.5/miscutils/chat.c:432
0m13.921s   0m3.396s  0m1.288s  busybox-1.18.5/editors/patch_bbox.c:6
0m3.546s    0m1.107s  0m1.301s  busybox-1.18.5/e2fsprogs/old_e2fsprogs/ext2fs/ismounted.c:129
0m7.123s    0m3.783s  0m1.303s  busybox-1.18.5/mailutils/mime.c:198
0m13.331s   0m2.433s  0m1.227s  busybox-1.18.5/coreutils/nohup.c:68
0m13.257s   0m3.024s  0m1.247s  busybox-1.18.5/util-linux/script.c:47
0m8.038s    0m4.521s  0m1.323s  busybox-1.18.5/networking/telnet.c:493
0m13.258s   0m2.300s  0m1.202s  busybox-1.18.5/libbb/chomp.c:11
0m17.268s   0m4.077s  0m1.313s  busybox-1.18.5/networking/arp.c:63
0m7.088s    0m2.885s  0m1.259s  busybox-1.18.5/sysklogd/logread.c:13
0m14.239s   0m1.367s  0m1.276s  busybox-1.18.5/archival/libarchive/decompress_uncompress.c:238
0m2.823s    0m0.998s  0m1.230s  busybox-1.18.5/e2fsprogs/old_e2fsprogs/ext2fs/lookup.c:56
0m7.637s    0m4.326s  0m1.300s  busybox-1.18.5/editors/patch_toybox.c:20
0m6.949s    0m2.560s  0m1.223s  busybox-1.18.5/util-linux/volume_id/xfs.c:28
0m2.364s    0m0.921s  0m1.242s  busybox-1.18.5/libbb/pw_encrypt_md5.c:32
0m16.992s   0m1.486s  0m1.248s  busybox-1.18.5/networking/udhcp/arpping.c:85
0m15.731s   0m2.562s  0m1.217s  busybox-1.18.5/networking/libiproute/ll_addr.c:14
3m48.838s   0m1.626s  0m1.334s  busybox-1.18.5/e2fsprogs/old_e2fsprogs/tune2fs.c:664
0m13.392s   0m2.342s  0m1.200s  busybox-1.18.5/libbb/match_fstype.c:15
0m3.143s    0m0.959s  0m1.267s  busybox-1.18.5/e2fsprogs/old_e2fsprogs/ext2fs/openfs.c:26
0m6.935s    0m2.033s  0m1.139s  busybox-1.18.5/libbb/bb_strtod.c:83
0m18.279s   0m5.416s  0m1.332s  busybox-1.18.5/networking/ntpd_simple.c:125
0m15.750s   0m1.354s  0m1.234s  busybox-1.18.5/networking/udhcp/static_leases.c:73
0m6.494s    0m1.365s  0m1.385s  busybox-1.18.5/miscutils/devfsd.c:568
0m3.390s    0m0.970s  0m1.234s  busybox-1.18.5/e2fsprogs/old_e2fsprogs/ext2fs/finddev.c:141
0m13.610s   0m2.968s  0m1.268s  busybox-1.18.5/networking/libiproute/ll_types.c:32
0m13.394s   0m1.413s  0m1.177s  busybox-1.18.5/archival/libarchive/data_align.c:2
0m6.343s    0m1.398s  0m1.225s  busybox-1.18.5/e2fsprogs/old_e2fsprogs/e2p/mntopts.c:62
0m2.319s    0m0.915s  0m1.256s  busybox-1.18.5/archival/libarchive/lzo1x_c.c:217
0m7.561s    0m3.577s  0m1.299s  busybox-1.18.5/libbb/copy_file.c:133
0m13.177s   0m2.504s  0m1.232s  busybox-1.18.5/coreutils/catv.c:71
0m6.925s    0m2.639s  0m1.247s  busybox-1.18.5/util-linux/volume_id/ocfs2.c:52
0m2.923s    0m0.966s  0m1.238s  busybox-1.18.5/e2fsprogs/old_e2fsprogs/blkid/cache.c:106
0m13.978s   0m1.428s  0m1.201s  busybox-1.18.5/archival/libarchive/filter_accept_reject_list.c:13
0m6.701s    0m2.284s  0m1.227s  busybox-1.18.5/util-linux/volume_id/xfs.c:55
0m6.365s    0m1.453s  0m1.389s  busybox-1.18.5/miscutils/devfsd.c:1314
0m21.642s   0m3.219s  0m1.255s  busybox-1.18.5/networking/slattach.c:110
0m13.402s   0m2.363s  0m1.189s  busybox-1.18.5/coreutils/true.c:12
0m6.975s    0m2.459s  0m1.218s  busybox-1.18.5/util-linux/volume_id/linux_swap.c:42
0m14.521s   0m1.348s  0m1.206s  busybox-1.18.5/archival/libarchive/find_list_entry.c:44
0m13.273s   0m1.451s  0m1.265s  busybox-1.18.5/networking/isrv.c:73
0m6.811s    0m2.419s  0m1.228s  busybox-1.18.5/coreutils/uuencode.c:12
0m17.192s   0m4.629s  0m1.310s  busybox-1.18.5/shell/math.c:520
0m2.893s    0m0.930s  0m1.246s  busybox-1.18.5/e2fsprogs/old_e2fsprogs/ext2fs/namei.c:7
0m3.240s    0m0.918s  0m1.272s  busybox-1.18.5/e2fsprogs/old_e2fsprogs/blkid/blkid_getsize.c:51
0m13.721s   0m2.833s  0m1.260s  busybox-1.18.5/libbb/inet_common.c:50
0m7.236s    0m2.737s  0m1.256s  busybox-1.18.5/networking/vconfig.c:81
0m7.428s    0m3.783s  0m1.284s  busybox-1.18.5/miscutils/fbsplash.c:312
0m13.292s   0m2.603s  0m1.260s  busybox-1.18.5/coreutils/touch.c:39
0m13.310s   0m2.165s  0m1.224s  busybox-1.18.5/libbb/bb_bswap_64.c:11
0m3.128s    0m1.006s  0m1.247s  busybox-1.18.5/e2fsprogs/old_e2fsprogs/ext2fs/ext2fs_inline.c:307
0m7.345s    0m3.818s  0m1.304s  busybox-1.18.5/init/bootchartd.c:81
0m3.088s    0m0.943s  0m1.239s  busybox-1.18.5/e2fsprogs/old_e2fsprogs/ext2fs/rs_bitmap.c:42
0m2.887s    0m0.946s  0m1.221s  busybox-1.18.5/e2fsprogs/old_e2fsprogs/ext2fs/alloc_stats.c:28
0m18.176s   0m2.921s  0m1.267s  busybox-1.18.5/networking/libiproute/ll_map.c:104
0m20.926s   0m3.149s  0m1.278s  busybox-1.18.5/networking/libiproute/utils.c:189
0m14.013s   0m2.913s  0m1.230s  busybox-1.18.5/libbb/make_directory.c:119
0m15.800s   0m1.373s  0m1.243s  busybox-1.18.5/networking/udhcp/static_leases.c:3
0m9.394s    0m7.291s  0m1.404s  busybox-1.18.5/networking/ftpd.c:1200
0m8.576s    0m5.887s  0m1.343s  busybox-1.18.5/miscutils/crond.c:60
0m2.563s    0m2.161s  0m1.240s  busybox-1.18.5/applets/applet_tables.c:38
0m18.243s   0m5.025s  0m1.344s  busybox-1.18.5/networking/ntpd_simple.c:483
0m14.103s   0m2.597s  0m1.231s  busybox-1.18.5/coreutils/ln.c:23
0m13.705s   0m2.331s  0m1.192s  busybox-1.18.5/libbb/isdirectory.c:5
0m7.135s    0m2.335s  0m1.240s  busybox-1.18.5/console-tools/dumpkmap.c:23
0m13.606s   0m2.255s  0m1.212s  busybox-1.18.5/libbb/mtab.c:39
0m3.367s    0m0.909s  0m1.243s  busybox-1.18.5/e2fsprogs/old_e2fsprogs/blkid/blkid_getsize.c:28
0m2.830s    0m0.939s  0m1.242s  busybox-1.18.5/e2fsprogs/old_e2fsprogs/ext2fs/freefs.c:92
0m7.009s    0m3.021s  0m1.256s  busybox-1.18.5/procps/kill.c:22
0m8.995s    0m7.032s  0m1.383s  busybox-1.18.5/networking/ftpd.c:475
0m13.592s   0m2.795s  0m1.284s  busybox-1.18.5/util-linux/script.c:173
0m6.952s    0m2.239s  0m1.241s  busybox-1.18.5/selinux/runcon.c:59
0m7.634s    0m3.223s  0m1.263s  busybox-1.18.5/util-linux/acpid.c:221
0m13.073s   0m2.500s  0m1.248s  busybox-1.18.5/loginutils/deluser.c:83
0m8.410s    0m4.503s  0m1.364s  busybox-1.18.5/coreutils/stat.c:514
0m6.918s    0m2.312s  0m1.232s  busybox-1.18.5/libbb/get_console.c:63
0m3.604s    0m1.025s  0m1.345s  busybox-1.18.5/e2fsprogs/old_e2fsprogs/blkid/probe.c:242
0m8.423s    0m4.519s  0m1.311s  busybox-1.18.5/util-linux/mkfs_ext2.c:533
0m9.132s    0m0.995s  0m1.232s  busybox-1.18.5/e2fsprogs/old_e2fsprogs/lsattr.c:86
0m10.279s   0m9.136s  0m1.408s  busybox-1.18.5/miscutils/hdparm.c:1845
0m6.959s    0m2.961s  0m1.266s  busybox-1.18.5/networking/hostname.c:51
0m13.022s   0m1.311s  0m1.208s  busybox-1.18.5/archival/libarchive/get_header_tar_gz.c:21
0m13.065s   0m2.387s  0m1.217s  busybox-1.18.5/libbb/fgets_str.c:86
0m3.335s    0m2.036s  0m1.242s  busybox-1.18.5/miscutils/last.c:68
0m8.367s    0m5.072s  0m1.359s  busybox-1.18.5/procps/powertop.c:241
0m3.036s    0m0.937s  0m1.231s  busybox-1.18.5/e2fsprogs/old_e2fsprogs/ext2fs/cmp_bitmaps.c:55
0m14.484s   0m2.610s  0m1.259s  busybox-1.18.5/libbb/udp_io.c:144
0m7.142s    0m3.462s  0m1.295s  busybox-1.18.5/util-linux/getopt.c:322
0m7.122s    0m1.355s  0m1.429s  busybox-1.18.5/archival/gzip.c:108
0m6.972s    0m2.408s  0m1.235s  busybox-1.18.5/libbb/get_console.c:46
0m7.324s    0m3.134s  0m1.281s  busybox-1.18.5/util-linux/volume_id/util.c:29
0m13.868s   0m1.375s  0m1.198s  busybox-1.18.5/archival/libarchive/header_list.c:7
0m7.036s    0m2.765s  0m1.264s  busybox-1.18.5/networking/vconfig.c:5
0m6.945s    0m2.542s  0m1.246s  busybox-1.18.5/e2fsprogs/tune2fs.c:6
0m6.369s    0m1.412s  0m1.268s  busybox-1.18.5/miscutils/flash_eraseall.c:190
0m7.086s    0m2.614s  0m1.230s  busybox-1.18.5/util-linux/volume_id/btrfs.c:87
0m13.627s   0m2.411s  0m1.236s  busybox-1.18.5/miscutils/setsid.c:45
0m13.549s   0m2.785s  0m1.244s  busybox-1.18.5/libbb/make_directory.c:35
0m13.751s   0m2.763s  0m1.256s  busybox-1.18.5/coreutils/tee.c:11
0m6.923s    0m2.626s  0m1.269s  busybox-1.18.5/util-linux/volume_id/reiserfs.c:107
0m6.656s    0m1.430s  0m1.439s  busybox-1.18.5/coreutils/stty.c:839
0m7.149s    0m2.209s  0m1.262s  busybox-1.18.5/libbb/utmp.c:111
0m14.358s   0m2.016s  0m1.268s  busybox-1.18.5/coreutils/chown.c:163
0m13.462s   0m2.043s  0m1.218s  busybox-1.18.5/libbb/bb_basename.c:5
0m7.375s    0m2.963s  0m1.274s  busybox-1.18.5/loginutils/adduser.c:155
0m5.409s    0m4.585s  0m1.336s  busybox-1.18.5/libbb/appletlib.c:140
0m6.178s    0m1.437s  0m1.214s  busybox-1.18.5/e2fsprogs/old_e2fsprogs/e2p/pe.c:29
0m2.322s    0m0.960s  0m1.269s  busybox-1.18.5/libbb/xatonum_template.c:173
0m3.506s    0m2.290s  0m1.325s  busybox-1.18.5/miscutils/last.c:20
0m7.300s    0m4.101s  0m1.350s  busybox-1.18.5/util-linux/fbset.c:277
0m13.977s   0m1.446s  0m1.243s  busybox-1.18.5/archival/libarchive/get_header_tar_bz2.c:3
0m6.908s    0m2.404s  0m1.256s  busybox-1.18.5/util-linux/volume_id/linux_raid.c:60
0m7.204s    0m2.841s  0m1.257s  busybox-1.18.5/debianutils/run_parts.c:149
0m7.372s    0m3.915s  0m1.281s  busybox-1.18.5/coreutils/tail.c:212
0m14.168s   0m1.362s  0m1.239s  busybox-1.18.5/archival/libarchive/get_header_tar_gz.c:11
0m6.580s    0m1.377s  0m1.233s  busybox-1.18.5/miscutils/eject.c:25
0m6.905s    0m2.583s  0m1.236s  busybox-1.18.5/coreutils/cat.c:40
0m3.139s    0m0.981s  0m1.240s  busybox-1.18.5/e2fsprogs/old_e2fsprogs/ext2fs/rs_bitmap.c:14
0m2.982s    0m0.965s  0m1.263s  busybox-1.18.5/e2fsprogs/old_e2fsprogs/ext2fs/swapfs.c:48
0m7.175s    0m1.454s  0m1.425s  busybox-1.18.5/archival/dpkg.c:898
0m6.303s    0m1.363s  0m1.283s  busybox-1.18.5/util-linux/readprofile.c:174
0m6.373s    0m1.505s  0m1.234s  busybox-1.18.5/e2fsprogs/old_e2fsprogs/e2p/fgetsetflags.c:16
0m17.278s   0m2.957s  0m1.275s  busybox-1.18.5/networking/libiproute/ll_map.c:174
0m6.910s    0m2.541s  0m1.240s  busybox-1.18.5/coreutils/sum.c:43
0m2.838s    0m0.921s  0m1.236s  busybox-1.18.5/e2fsprogs/old_e2fsprogs/ext2fs/dblist_dir.c:46
0m13.694s   0m2.331s  0m1.215s  busybox-1.18.5/shell/random.c:37
0m6.984s    0m2.579s  0m1.257s  busybox-1.18.5/libbb/login.c:11
0m13.708s   0m1.352s  0m1.218s  busybox-1.18.5/archival/libarchive/get_header_tar_bz2.c:10
0m13.309s   0m1.609s  0m1.264s  busybox-1.18.5/archival/libarchive/lzo1x_1o.c:5
0m7.295s    0m3.165s  0m1.294s  busybox-1.18.5/util-linux/volume_id/util.c:146
0m7.620s    0m4.729s  0m1.373s  busybox-1.18.5/procps/iostat.c:99
0m6.432s    0m1.461s  0m1.245s  busybox-1.18.5/e2fsprogs/old_e2fsprogs/e2p/uuid.c:77
0m3.228s    0m0.981s  0m1.330s  busybox-1.18.5/e2fsprogs/old_e2fsprogs/ext2fs/rw_bitmaps.c:286
0m3.017s    0m0.966s  0m1.466s  busybox-1.18.5/e2fsprogs/old_e2fsprogs/ext2fs/cmp_bitmaps.c:24
0m7.410s    0m2.326s  0m1.381s  busybox-1.18.5/networking/ipcalc.c:75
0m6.133s    0m1.383s  0m1.250s  busybox-1.18.5/e2fsprogs/old_e2fsprogs/e2p/parse_num.c:5
0m6.842s    0m1.778s  0m1.247s  busybox-1.18.5/sysklogd/syslogd_and_logger.c:45
0m6.263s    0m1.396s  0m1.273s  busybox-1.18.5/miscutils/nandwrite.c:198
0m2.657s    0m1.054s  0m1.390s  busybox-1.18.5/util-linux/fdisk_sgi.c:551
0m13.206s   0m2.510s  0m1.238s  busybox-1.18.5/libbb/simplify_path.c:22
0m7.429s    0m2.945s  0m1.298s  busybox-1.18.5/networking/nameif.c:13
0m6.257s    0m1.381s  0m1.234s  busybox-1.18.5/e2fsprogs/old_e2fsprogs/e2p/hashstr.c:62
0m3.579s    0m2.187s  0m1.270s  busybox-1.18.5/miscutils/last.c:79
0m2.910s    0m0.963s  0m1.318s  busybox-1.18.5/e2fsprogs/old_e2fsprogs/ext2fs/dirblock.c:81
0m13.137s   0m2.261s  0m1.326s  busybox-1.18.5/coreutils/rm.c:36
0m2.114s    0m1.486s  0m1.292s  busybox-1.18.5/coreutils/test_ptr_hack.c:19
0m7.114s    0m1.368s  0m1.311s  busybox-1.18.5/archival/bzip2.c:33
0m2.366s    0m0.966s  0m1.358s  busybox-1.18.5/archival/libarchive/bz/compress.c:447
0m2.956s    0m0.950s  0m1.308s  busybox-1.18.5/e2fsprogs/old_e2fsprogs/ext2fs/dir_iterate.c:37
0m13.360s   0m2.288s  0m1.271s  busybox-1.18.5/coreutils/tty.c:14
0m4.232s    0m1.359s  0m1.325s  busybox-1.18.5/e2fsprogs/old_e2fsprogs/uuid/gen_uuid.c:169
0m6.957s    0m2.340s  0m1.247s  busybox-1.18.5/libbb/safe_gethostname.c:44
0m3.028s    0m0.961s  0m1.305s  busybox-1.18.5/e2fsprogs/old_e2fsprogs/ext2fs/swapfs.c:197
0m2.990s    0m0.966s  0m1.271s  busybox-1.18.5/e2fsprogs/old_e2fsprogs/ext2fs/badblocks.c:165
0m3.159s    0m1.055s  0m1.262s  busybox-1.18.5/e2fsprogs/old_e2fsprogs/blkid/resolve.c:43
0m2.815s    0m0.961s  0m1.249s  busybox-1.18.5/e2fsprogs/old_e2fsprogs/ext2fs/sparse.c:5
0m0.394s    0m0.391s  0m0.391s  busybox-1.18.5/modutils/modutils-24.c:2765
0m2.373s    0m0.869s  0m1.274s  busybox-1.18.5/util-linux/fdisk_aix.c:10
0m13.430s   0m2.285s  0m1.253s  busybox-1.18.5/coreutils/fsync.c:42
0m6.790s    0m2.307s  0m1.261s  busybox-1.18.5/util-linux/pivot_root.c:18
0m7.493s    0m3.198s  0m1.289s  busybox-1.18.5/util-linux/acpid.c:53
0m6.293s    0m1.423s  0m1.322s  busybox-1.18.5/coreutils/du.c:21
0m8.152s    0m4.728s  0m1.369s  busybox-1.18.5/util-linux/mkfs_minix.c:221
0m8.436s    0m4.519s  0m1.290s  busybox-1.18.5/networking/ifplugd.c:353
0m8.019s    0m4.670s  0m1.374s  busybox-1.18.5/loginutils/getty.c:587
0m13.261s   0m2.240s  0m1.222s  busybox-1.18.5/libbb/get_volsize.c:7
0m2.334s    0m0.918s  0m1.292s  busybox-1.18.5/archival/libarchive/bz/huffman.c:67
0m43.809s   0m2.541s  0m1.265s  busybox-1.18.5/modutils/lsmod.c:109
0m7.059s    0m2.403s  0m1.258s  busybox-1.18.5/selinux/matchpathcon.c:33
0m18.769s   0m6.626s  0m1.395s  busybox-1.18.5/init/init.c:600
0m16.301s   0m2.518s  0m1.260s  busybox-1.18.5/libbb/platform.c:107
0m6.859s    0m2.065s  0m1.269s  busybox-1.18.5/miscutils/chrt.c:45
0m13.344s   0m2.214s  0m1.251s  busybox-1.18.5/util-linux/setarch.c:1
0m6.974s    0m2.440s  0m1.226s  busybox-1.18.5/util-linux/volume_id/unused_silicon_raid.c:42
0m7.279s    0m2.592s  0m1.269s  busybox-1.18.5/libbb/login.c:119
0m15.165s   0m2.752s  0m1.304s  busybox-1.18.5/libbb/read_printf.c:207
0m14.419s   0m3.898s  0m1.344s  busybox-1.18.5/libbb/xconnect.c:351
0m4.387s    0m1.791s  0m1.756s  busybox-1.18.5/shell/hush.c:1417
0m6.977s    0m2.450s  0m1.264s  busybox-1.18.5/e2fsprogs/tune2fs.c:12
0m3.127s    0m0.948s  0m1.282s  busybox-1.18.5/e2fsprogs/old_e2fsprogs/blkid/resolve.c:131
0m18.093s   0m2.740s  0m1.271s  busybox-1.18.5/modutils/modutils.c:122
0m13.113s   0m2.346s  0m1.260s  busybox-1.18.5/init/mesg.c:7
0m7.565s    0m3.978s  0m1.352s  busybox-1.18.5/networking/dnsd.c:176
0m12.965s   0m2.359s  0m1.268s  busybox-1.18.5/procps/watch.c:12
0m13.366s   0m2.374s  0m1.284s  busybox-1.18.5/coreutils/rmdir.c:11
0m2.562s    0m1.070s  0m1.427s  busybox-1.18.5/util-linux/fdisk_sgi.c:384
0m13.313s   0m2.230s  0m1.257s  busybox-1.18.5/libbb/xgethostbyname.c:10
0m7.383s    0m3.954s  0m1.340s  busybox-1.18.5/init/bootchartd.c:331
0m12.847s   0m2.542s  0m1.267s  busybox-1.18.5/libbb/getpty.c:14
0m14.582s   0m2.918s  0m1.311s  busybox-1.18.5/libbb/read_printf.c:123
0m13.929s   0m4.182s  0m1.345s  busybox-1.18.5/libbb/xconnect.c:276
0m7.106s    0m2.666s  0m1.257s  busybox-1.18.5/util-linux/volume_id/unused_lvm.c:63
0m15.937s   0m4.890s  0m1.346s  busybox-1.18.5/modutils/modprobe.c:324
2m15.920s   0m1.369s  0m1.358s  busybox-1.18.5/networking/tc.c:288
0m3.102s    0m1.025s  0m1.232s  busybox-1.18.5/e2fsprogs/old_e2fsprogs/uuid/parse.c:25
0m2.470s    0m0.950s  0m1.381s  busybox-1.18.5/archival/libarchive/bz/blocksort.c:177
0m6.906s    0m2.476s  0m1.241s  busybox-1.18.5/util-linux/volume_id/unused_lvm.c:54
0m2.337s    0m0.925s  0m1.284s  busybox-1.18.5/archival/libarchive/lzo1x_c.c:62
0m15.876s   0m1.393s  0m1.310s  busybox-1.18.5/networking/udhcp/files.c:112
0m45.774s   0m2.496s  0m1.271s  busybox-1.18.5/modutils/lsmod.c:94
0m8.010s    0m4.881s  0m1.375s  busybox-1.18.5/loginutils/getty.c:262
0m2.970s    0m1.017s  0m1.283s  busybox-1.18.5/e2fsprogs/old_e2fsprogs/ext2fs/bmap.c:36
0m14.130s   0m2.603s  0m1.262s  busybox-1.18.5/libbb/process_escape_sequence.c:1
0m6.394s    0m1.381s  0m1.436s  busybox-1.18.5/miscutils/devfsd.c:599
0m3.258s    0m1.045s  0m1.290s  busybox-1.18.5/e2fsprogs/old_e2fsprogs/blkid/devname.c:3
0m3.362s    0m1.840s  0m1.267s  busybox-1.18.5/networking/httpd_ssi.c:42
0m13.738s   0m2.311s  0m1.249s  busybox-1.18.5/coreutils/printenv.c:15
0m13.171s   0m1.339s  0m1.298s  busybox-1.18.5/networking/isrv.c:14
0m12.972s   0m2.362s  0m1.244s  busybox-1.18.5/libbb/read.c:21
0m6.942s    0m2.481s  0m1.244s  busybox-1.18.5/util-linux/volume_id/unused_minix.c:43
0m13.759s   0m1.325s  0m1.217s  busybox-1.18.5/archival/libarchive/data_align.c:12
0m7.528s    0m1.422s  0m1.298s  busybox-1.18.5/archival/cpio.c:383
0m13.335s   0m2.533s  0m1.251s  busybox-1.18.5/libbb/llist.c:65
0m2.488s    0m0.939s  0m1.400s  busybox-1.18.5/archival/libarchive/bz/blocksort.c:516
0m13.947s   0m1.330s  0m1.373s  busybox-1.18.5/archival/lzop.c:401
0m3.825s    0m1.657s  0m1.235s  busybox-1.18.5/libbb/perror_nomsg_and_die.c:18
0m3.174s    0m0.973s  0m1.285s  busybox-1.18.5/e2fsprogs/old_e2fsprogs/ext2fs/openfs.c:106
0m13.199s   0m2.213s  0m1.237s  busybox-1.18.5/libbb/last_char_is.c:1
0m8.917s    0m7.481s  0m1.423s  busybox-1.18.5/util-linux/fsck_minix.c:862
0m2.912s    0m0.967s  0m1.287s  busybox-1.18.5/e2fsprogs/old_e2fsprogs/ext2fs/res_gdt.c:114
0m7.075s    0m2.793s  0m1.285s  busybox-1.18.5/miscutils/adjtimex.c:29
0m13.213s   0m1.323s  0m1.276s  busybox-1.18.5/archival/libarchive/header_verbose_list.c:66
0m13.452s   0m2.619s  0m1.280s  busybox-1.18.5/coreutils/nohup.c:63
0m3.657s    0m1.096s  0m1.635s  busybox-1.18.5/e2fsprogs/old_e2fsprogs/ext2fs/getsize.c:84
0m13.607s   0m1.423s  0m1.241s  busybox-1.18.5/archival/libarchive/filter_accept_list_reassign.c:3
0m14.253s   0m1.488s  0m1.223s  busybox-1.18.5/archival/libarchive/seek_by_read.c:11
0m14.698s   0m2.700s  0m1.204s  busybox-1.18.5/networking/tunctl.c:103
0m3.344s    0m0.973s  0m1.287s  busybox-1.18.5/e2fsprogs/old_e2fsprogs/ext2fs/block.c:339
0m13.602s   0m2.516s  0m1.281s  busybox-1.18.5/loginutils/cryptpw.c:41
0m14.678s   0m2.367s  0m1.242s  busybox-1.18.5/libbb/get_cpu_count.c:11
0m2.894s    0m0.983s  0m1.247s  busybox-1.18.5/e2fsprogs/old_e2fsprogs/ext2fs/bb_compat.c:13
0m13.939s   0m1.445s  0m1.219s  busybox-1.18.5/archival/libarchive/filter_accept_all.c:6
0m6.912s    0m1.494s  0m1.370s  busybox-1.18.5/util-linux/mdev.c:85
0m7.091s    0m2.541s  0m1.292s  busybox-1.18.5/coreutils/who.c:68
0m12.940s   0m2.522s  0m1.255s  busybox-1.18.5/modutils/insmod.c:28
0m2.085s    0m1.077s  0m1.255s  busybox-1.18.5/applets/usage.c:11
0m2.504s    0m0.966s  0m1.482s  busybox-1.18.5/util-linux/fdisk_osf.c:416
0m2.137s    0m1.097s  0m1.244s  busybox-1.18.5/applets/usage.c:51
0m6.886s    0m2.911s  0m1.283s  busybox-1.18.5/util-linux/hexdump.c:108
0m6.979s    0m2.516s  0m1.278s  busybox-1.18.5/selinux/runcon.c:27
0m13.330s   0m2.446s  0m1.251s  busybox-1.18.5/coreutils/sync.c:7
0m8.654s    0m6.757s  0m1.394s  busybox-1.18.5/runit/svlogd.c:388
0m12.932s   0m2.508s  0m1.257s  busybox-1.18.5/libbb/wfopen_input.c:32
0m8.299s    0m6.256s  0m1.380s  busybox-1.18.5/editors/ed.c:353
0m23.333s   0m2.994s  0m1.280s  busybox-1.18.5/coreutils/wc.c:137
0m7.540s    0m3.392s  0m1.301s  busybox-1.18.5/util-linux/mkfs_reiser.c:225
0m3.176s    0m0.985s  0m1.233s  busybox-1.18.5/e2fsprogs/old_e2fsprogs/ext2fs/valid_blk.c:20
0m13.229s   0m2.919s  0m1.258s  busybox-1.18.5/libpwdgrp/uidgid_get.c:8
0m2.932s    0m0.943s  0m1.277s  busybox-1.18.5/e2fsprogs/old_e2fsprogs/ext2fs/dirhash.c:1
0m13.302s   0m2.573s  0m1.255s  busybox-1.18.5/loginutils/deluser.c:60
0m13.547s   0m2.314s  0m1.286s  busybox-1.18.5/libbb/parse_config.c:138
0m13.961s   0m3.862s  0m1.338s  busybox-1.18.5/libbb/hash_md5prime.c:14
0m13.755s   0m2.792s  0m1.295s  busybox-1.18.5/util-linux/script.c:187
0m13.277s   0m2.292s  0m1.241s  busybox-1.18.5/libbb/safe_strncpy.c:32
0m2.566s    0m1.034s  0m1.407s  busybox-1.18.5/util-linux/fdisk_osf.c:578
0m6.057s    0m1.470s  0m1.332s  busybox-1.18.5/runit/runsvdir.c:20
0m2.976s    0m0.940s  0m1.270s  busybox-1.18.5/e2fsprogs/old_e2fsprogs/ext2fs/cmp_bitmaps.c:61
0m13.553s   0m2.381s  0m1.237s  busybox-1.18.5/shell/random.c:16
0m13.049s   0m2.236s  0m1.246s  busybox-1.18.5/miscutils/ttysize.c:36
0m13.965s   0m1.350s  0m1.268s  busybox-1.18.5/libbb/rtc.c:78
0m6.678s    0m1.475s  0m1.220s  busybox-1.18.5/e2fsprogs/old_e2fsprogs/e2p/pe.c:8
0m12.847s   0m2.409s  0m1.246s  busybox-1.18.5/libbb/setup_environment.c:58
0m8.008s    0m4.898s  0m1.362s  busybox-1.18.5/loginutils/getty.c:604
0m7.078s    0m2.213s  0m1.279s  busybox-1.18.5/shell/cttyhack.c:33
0m6.911s    0m2.743s  0m1.257s  busybox-1.18.5/coreutils/dos2unix.c:4
0m13.305s   0m2.327s  0m1.236s  busybox-1.18.5/coreutils/sync.c:25
0m13.000s   0m2.189s  0m1.240s  busybox-1.18.5/selinux/load_policy.c:7
0m8.987s    0m7.411s  0m1.425s  busybox-1.18.5/util-linux/fsck_minix.c:1266
0m6.460s    0m1.444s  0m1.387s  busybox-1.18.5/archival/libarchive/decompress_bunzip2.c:68
0m6.900s    0m2.725s  0m1.264s  busybox-1.18.5/util-linux/volume_id/iso9660.c:70
0m7.204s    0m2.467s  0m1.259s  busybox-1.18.5/coreutils/comm.c:55
0m13.774s   0m2.456s  0m1.247s  busybox-1.18.5/libbb/remove_file.c:22
0m7.240s    0m3.427s  0m1.327s  busybox-1.18.5/util-linux/getopt.c:230
0m2.916s    0m0.939s  0m1.267s  busybox-1.18.5/e2fsprogs/old_e2fsprogs/ext2fs/flushb.c:78
0m13.489s   0m2.572s  0m1.322s  busybox-1.18.5/coreutils/ln.c:49
0m7.116s    0m1.354s  0m1.633s  busybox-1.18.5/archival/libarchive/decompress_unlzma.c:207
0m18.874s   0m2.853s  0m1.314s  busybox-1.18.5/modutils/modutils.c:136
0m2.406s    0m0.897s  0m1.342s  busybox-1.18.5/libbb/pw_encrypt_sha.c:104
0m6.575s    0m1.389s  0m1.655s  busybox-1.18.5/networking/telnetd.c:439
0m15.813s   0m3.118s  0m1.311s  busybox-1.18.5/networking/libiproute/rt_names.c:185
0m2.879s    0m1.006s  0m1.259s  busybox-1.18.5/e2fsprogs/old_e2fsprogs/ext2fs/alloc_stats.c:3
0m6.176s    0m1.313s  0m1.279s  busybox-1.18.5/libbb/find_root_device.c:73
0m8.266s    0m5.890s  0m1.590s  busybox-1.18.5/editors/diff.c:158
0m2.948s    0m0.961s  0m1.383s  busybox-1.18.5/e2fsprogs/old_e2fsprogs/ext2fs/dirhash.c:92
0m13.289s   0m2.566s  0m1.275s  busybox-1.18.5/libbb/fgets_str.c:58
0m14.590s   0m1.426s  0m1.311s  busybox-1.18.5/libbb/rtc.c:62
0m13.010s   0m2.251s  0m1.283s  busybox-1.18.5/libbb/concat_subpath_file.c:9
0m7.486s    0m4.184s  0m1.352s  busybox-1.18.5/util-linux/fbset.c:207
0m7.404s    0m3.881s  0m1.344s  busybox-1.18.5/debianutils/start_stop_daemon.c:337
0m7.328s    0m4.352s  0m1.365s  busybox-1.18.5/networking/dnsd.c:326
0m2.941s    0m0.950s  0m1.251s  busybox-1.18.5/e2fsprogs/old_e2fsprogs/ext2fs/gen_bitmap.c:29
0m7.355s    0m1.497s  0m1.421s  busybox-1.18.5/miscutils/devfsd.c:271
0m3.880s    0m1.696s  0m1.237s  busybox-1.18.5/libbb/perror_nomsg.c:19
0m6.945s    0m2.271s  0m1.246s  busybox-1.18.5/miscutils/wall.c:2
0m8.361s    0m5.509s  0m1.373s  busybox-1.18.5/networking/ping.c:517
0m7.157s    0m3.590s  0m1.317s  busybox-1.18.5/miscutils/man.c:285
0m3.095s    0m1.096s  0m1.263s  busybox-1.18.5/e2fsprogs/old_e2fsprogs/blkid/resolve.c:19
0m13.047s   0m2.437s  0m1.223s  busybox-1.18.5/libbb/safe_write.c:15
0m14.328s   0m4.749s  0m1.360s  busybox-1.18.5/libbb/xfuncs_printf.c:511
0m6.920s    0m2.316s  0m1.254s  busybox-1.18.5/util-linux/volume_id/unused_lsi_raid.c:46
0m12.809s   0m2.053s  0m1.286s  busybox-1.18.5/coreutils/cksum.c:16
0m8.185s    0m5.420s  0m1.361s  busybox-1.18.5/coreutils/test.c:610
0m13.478s   0m2.275s  0m1.277s  busybox-1.18.5/selinux/getenforce.c:4
0m14.378s   0m3.717s  0m1.321s  busybox-1.18.5/coreutils/sort.c:395
0m13.933s   0m1.551s  0m1.223s  busybox-1.18.5/archival/libarchive/filter_accept_list.c:18
0m7.094s    0m2.718s  0m1.286s  busybox-1.18.5/networking/nbd-client.c:56
0m9.167s    0m1.135s  0m1.280s  busybox-1.18.5/e2fsprogs/old_e2fsprogs/chattr.c:25
0m14.980s   0m5.875s  0m1.462s  busybox-1.18.5/archival/libarchive/lzo1x_9x.c:77
0m7.246s    0m3.357s  0m1.287s  busybox-1.18.5/util-linux/volume_id/fat.c:69
5m40.617s   0m1.508s  0m1.264s  busybox-1.18.5/archival/libarchive/decompress_unxz.c:64
0m6.656s    0m2.234s  0m1.226s  busybox-1.18.5/util-linux/blkid.c:3
0m13.340s   0m2.292s  0m1.262s  busybox-1.18.5/libbb/device_open.c:6
0m7.080s    0m2.531s  0m1.246s  busybox-1.18.5/util-linux/volume_id/xfs.c:2
0m3.255s    0m0.970s  0m1.306s  busybox-1.18.5/e2fsprogs/old_e2fsprogs/ext2fs/imager.c:366
0m3.720s    0m1.683s  0m1.262s  busybox-1.18.5/libbb/perror_nomsg_and_die.c:17
0m2.665s    0m1.053s  0m1.363s  busybox-1.18.5/util-linux/fdisk_sgi.c:439
0m3.004s    0m0.958s  0m1.219s  busybox-1.18.5/e2fsprogs/old_e2fsprogs/ext2fs/link.c:86
0m2.843s    0m0.942s  0m1.213s  busybox-1.18.5/e2fsprogs/old_e2fsprogs/ext2fs/lookup.c:12
0m2.303s    0m0.928s  0m1.248s  busybox-1.18.5/archival/libarchive/bz/huffman.c:134
0m2.963s    0m0.979s  0m1.225s  busybox-1.18.5/e2fsprogs/old_e2fsprogs/ext2fs/io_manager.c:57
0m7.188s    0m2.705s  0m1.264s  busybox-1.18.5/networking/ftpgetput.c:280
0m13.745s   0m2.219s  0m1.211s  busybox-1.18.5/coreutils/tty.c:27
0m7.121s    0m2.877s  0m1.248s  busybox-1.18.5/util-linux/volume_id/ntfs.c:50
0m7.324s    0m2.384s  0m1.303s  busybox-1.18.5/init/halt.c:77
0m13.745s   0m2.769s  0m1.325s  busybox-1.18.5/networking/libiproute/ll_types.c:18
0m2.797s    0m0.940s  0m1.212s  busybox-1.18.5/e2fsprogs/old_e2fsprogs/ext2fs/write_bb_file.c:3
0m13.327s   0m2.320s  0m1.233s  busybox-1.18.5/console-tools/resize.c:24
0m8.388s    0m5.316s  0m1.366s  busybox-1.18.5/networking/ping.c:524
0m13.442s   0m2.397s  0m1.263s  busybox-1.18.5/libbb/xgetcwd.c:16
0m25.084s   0m2.538s  0m1.252s  busybox-1.18.5/util-linux/rev.c:44
0m14.397s   0m1.828s  0m1.241s  busybox-1.18.5/libbb/strrstr.c:65
0m13.199s   0m2.641s  0m1.255s  busybox-1.18.5/libbb/copyfd.c:86
0m2.928s    0m0.987s  0m1.240s  busybox-1.18.5/e2fsprogs/old_e2fsprogs/ext2fs/mkdir.c:98
0m6.025s    0m1.317s  0m1.224s  busybox-1.18.5/libbb/find_root_device.c:23
0m13.985s   0m2.336s  0m1.222s  busybox-1.18.5/libbb/perror_msg.c:22
0m16.376s   0m1.399s  0m1.268s  busybox-1.18.5/networking/udhcp/packet.c:172
0m14.174s   0m2.331s  0m1.207s  busybox-1.18.5/libbb/match_fstype.c:16
0m2.948s    0m0.940s  0m1.222s  busybox-1.18.5/e2fsprogs/old_e2fsprogs/uuid/unpack.c:10
0m12.908s   0m2.051s  0m1.265s  busybox-1.18.5/networking/tunctl.c:23
0m7.418s    0m3.913s  0m1.314s  busybox-1.18.5/debianutils/start_stop_daemon.c:245
0m13.762s   0m2.266s  0m1.227s  busybox-1.18.5/console-tools/clear.c:9
0m13.316s   0m2.388s  0m1.232s  busybox-1.18.5/miscutils/strings.c:57
0m2.230s    0m0.861s  0m1.231s  busybox-1.18.5/libpwdgrp/pwd_grp_internal.c:22
0m6.717s    0m2.788s  0m1.257s  busybox-1.18.5/util-linux/mkswap.c:85
0m9.609s    0m8.474s  0m1.452s  busybox-1.18.5/miscutils/less.c:887
0m13.380s   0m2.374s  0m1.296s  busybox-1.18.5/coreutils/logname.c:13
0m7.152s    0m2.923s  0m1.290s  busybox-1.18.5/util-linux/volume_id/unused_ufs.c:45
0m2.979s    0m0.943s  0m1.239s  busybox-1.18.5/e2fsprogs/old_e2fsprogs/ext2fs/bb_compat.c:52
0m2.216s    0m1.100s  0m1.263s  busybox-1.18.5/applets/usage.c:55
0m6.844s    0m1.507s  0m1.370s  busybox-1.18.5/archival/unzip.c:464
0m7.185s    0m2.720s  0m1.274s  busybox-1.18.5/miscutils/flashcp.c:141
0m13.715s   0m2.355s  0m1.235s  busybox-1.18.5/modutils/insmod.c:2
0m2.971s    0m0.910s  0m1.231s  busybox-1.18.5/e2fsprogs/old_e2fsprogs/uuid/unpack.c:59
0m32.007s   0m3.826s  0m1.316s  busybox-1.18.5/networking/libiproute/iplink.c:160
0m13.576s   0m1.368s  0m1.293s  busybox-1.18.5/util-linux/switch_root.c:115
0m8.028s    0m4.320s  0m1.371s  busybox-1.18.5/procps/ps.c:4
0m13.818s   0m2.830s  0m1.266s  busybox-1.18.5/coreutils/mv.c:18
0m7.989s    0m5.272s  0m1.374s  busybox-1.18.5/util-linux/mkfs_minix.c:36
0m13.534s   0m2.680s  0m1.288s  busybox-1.18.5/coreutils/chmod.c:119
0m7.264s    0m1.357s  0m1.439s  busybox-1.18.5/miscutils/devfsd.c:1661
0m8.946s    0m8.229s  0m1.391s  busybox-1.18.5/coreutils/od_bloaty.c:434
0m13.137s   0m2.307s  0m1.249s  busybox-1.18.5/coreutils/basename.c:26
0m44.864s   0m5.942s  0m1.408s  busybox-1.18.5/coreutils/ls.c:980
0m6.789s    0m2.148s  0m1.232s  busybox-1.18.5/libbb/bb_qsort.c:10
0m2.964s    0m0.951s  0m1.281s  busybox-1.18.5/e2fsprogs/old_e2fsprogs/ext2fs/dblist.c:236
0m13.744s   0m2.341s  0m1.290s  busybox-1.18.5/libbb/parse_config.c:185
0m2.857s    0m0.935s  0m1.261s  busybox-1.18.5/e2fsprogs/old_e2fsprogs/ext2fs/lookup.c:23
0m13.809s   0m2.067s  0m1.291s  busybox-1.18.5/networking/tunctl.c:68
0m8.529s    0m6.003s  0m1.399s  busybox-1.18.5/procps/mpstat.c:881
0m7.071s    0m2.329s  0m1.238s  busybox-1.18.5/util-linux/volume_id/unused_silicon_raid.c:50
0m13.228s   0m2.225s  0m1.237s  busybox-1.18.5/libbb/ask_confirmation.c:25
0m7.832s    0m4.550s  0m1.346s  busybox-1.18.5/util-linux/mkfs_vfat.c:392
0m6.607s    0m1.176s  0m1.293s  busybox-1.18.5/e2fsprogs/old_e2fsprogs/ext2fs/mkjournal.c:155
0m13.164s   0m2.682s  0m1.251s  busybox-1.18.5/libbb/remove_file.c:53
0m7.484s    0m2.296s  0m1.249s  busybox-1.18.5/console-tools/kbd_mode.c:15
0m13.466s   0m2.921s  0m1.279s  busybox-1.18.5/coreutils/mv.c:142
0m7.332s    0m2.836s  0m1.267s  busybox-1.18.5/shell/match.c:44
0m16.153s   0m6.878s  0m1.448s  busybox-1.18.5/networking/traceroute.c:623
0m15.918s   0m1.397s  0m1.277s  busybox-1.18.5/networking/nc.c:47
0m6.271s    0m1.346s  0m1.271s  busybox-1.18.5/util-linux/fdformat.c:36
0m13.891s   0m2.205s  0m1.226s  busybox-1.18.5/libbb/bb_basename.c:9
0m6.300s    0m1.413s  0m1.265s  busybox-1.18.5/e2fsprogs/old_e2fsprogs/e2p/feature.c:167
0m6.931s    0m2.527s  0m1.256s  busybox-1.18.5/util-linux/volume_id/linux_swap.c:63
0m3.017s    0m0.946s  0m1.287s  busybox-1.18.5/e2fsprogs/old_e2fsprogs/ext2fs/block.c:176
0m7.462s    0m3.169s  0m1.306s  busybox-1.18.5/networking/brctl.c:158
0m2.603s    0m1.535s  0m1.228s  busybox-1.18.5/shell/ash_test/printenv.c:26
0m7.033s    0m2.678s  0m1.295s  busybox-1.18.5/util-linux/mkswap.c:38
0m13.404s   0m2.229s  0m1.231s  busybox-1.18.5/libbb/fclose_nonstdin.c:11
0m12.976s   0m2.059s  0m1.265s  busybox-1.18.5/networking/tunctl.c:10
0m6.850s    0m1.930s  0m1.236s  busybox-1.18.5/libbb/selinux_common.c:37
0m13.241s   0m2.533s  0m1.264s  busybox-1.18.5/miscutils/volname.c:27
0m13.361s   0m2.083s  0m1.253s  busybox-1.18.5/coreutils/cksum.c:49
0m13.690s   0m3.318s  0m1.310s  busybox-1.18.5/coreutils/echo.c:300
0m7.771s    0m4.555s  0m1.374s  busybox-1.18.5/libbb/procps.c:88
0m29.435s   0m31.507s 0m1.573s  busybox-1.18.5/editors/vi.c:485
0m6.858s    0m2.389s  0m1.243s  busybox-1.18.5/util-linux/volume_id/unused_isw_raid.c:49
0m7.108s    0m2.096s  0m1.276s  busybox-1.18.5/libbb/utmp.c:66
0m6.242s    0m1.363s  0m1.262s  busybox-1.18.5/archival/libarchive/data_extract_to_command.c:105
0m7.386s    0m1.546s  0m1.482s  busybox-1.18.5/archival/gzip.c:836
0m13.788s   0m1.362s  0m1.241s  busybox-1.18.5/archival/libarchive/get_header_tar_bz2.c:19
0m7.112s    0m2.478s  0m1.255s  busybox-1.18.5/console-tools/loadkmap.c:48
0m6.732s    0m1.562s  0m1.381s  busybox-1.18.5/networking/udhcp/common.c:441
0m13.571s   0m2.227s  0m1.241s  busybox-1.18.5/applets/applets.c:13
0m2.827s    0m0.937s  0m1.269s  busybox-1.18.5/e2fsprogs/old_e2fsprogs/ext2fs/unlink.c:40
0m13.635s   0m2.247s  0m1.220s  busybox-1.18.5/libbb/safe_write.c:13
0m13.657s   0m3.489s  0m1.290s  busybox-1.18.5/editors/patch_bbox.c:274
0m6.519s    0m1.493s  0m1.251s  busybox-1.18.5/miscutils/eject.c:88
0m7.133s    0m3.395s  0m1.291s  busybox-1.18.5/procps/sysctl.c:213
0m3.054s    0m0.940s  0m1.264s  busybox-1.18.5/e2fsprogs/old_e2fsprogs/blkid/resolve.c:137
0m13.424s   0m2.311s  0m1.226s  busybox-1.18.5/libbb/perror_msg.c:33
0m2.469s    0m0.971s  0m1.370s  busybox-1.18.5/archival/libarchive/bz/blocksort.c:214
0m13.427s   0m2.446s  0m1.240s  busybox-1.18.5/coreutils/rmdir.c:32
0m14.384s   0m1.606s  0m1.242s  busybox-1.18.5/archival/libarchive/lzo1x_1.c:5
0m20.688s   0m3.421s  0m1.311s  busybox-1.18.5/networking/libiproute/iprule.c:118
0m7.235s    0m3.386s  0m1.321s  busybox-1.18.5/util-linux/volume_id/util.c:102
0m15.369s   0m2.796s  0m1.276s  busybox-1.18.5/coreutils/mv.c:59
0m7.030s    0m2.724s  0m1.292s  busybox-1.18.5/miscutils/microcom.c:117
0m7.191s    0m2.854s  0m1.254s  busybox-1.18.5/util-linux/volume_id/unused_msdos.c:35
0m16.365s   0m1.370s  0m1.284s  busybox-1.18.5/networking/nc.c:154
0m2.863s    0m0.959s  0m1.376s  busybox-1.18.5/libbb/pw_encrypt_des.c:680
0m14.381s   0m1.399s  0m1.227s  busybox-1.18.5/archival/libarchive/filter_accept_reject_list.c:26
0m8.137s    0m4.203s  0m1.334s  busybox-1.18.5/util-linux/mkfs_vfat.c:16
0m6.986s    0m2.763s  0m1.289s  busybox-1.18.5/util-linux/volume_id/ntfs.c:158
0m7.206s    0m2.332s  0m1.299s  busybox-1.18.5/util-linux/volume_id/volume_id.c:186
0m2.805s    0m0.990s  0m1.258s  busybox-1.18.5/e2fsprogs/old_e2fsprogs/ext2fs/link.c:25
0m10.662s   0m8.484s  0m1.431s  busybox-1.18.5/miscutils/less.c:1281
0m2.994s    0m0.982s  0m1.246s  busybox-1.18.5/e2fsprogs/old_e2fsprogs/ext2fs/newdir.c:44
0m3.098s    0m0.962s  0m1.281s  busybox-1.18.5/e2fsprogs/old_e2fsprogs/ext2fs/dirhash.c:14
0m3.299s    0m1.077s  0m1.286s  busybox-1.18.5/e2fsprogs/old_e2fsprogs/ext2fs/imager.c:117
0m2.405s    0m0.960s  0m1.277s  busybox-1.18.5/archival/libarchive/bz/huffman.c:201
0m9.090s    0m4.609s  0m1.340s  busybox-1.18.5/networking/zcip.c:490
0m17.897s   0m3.857s  0m1.334s  busybox-1.18.5/libbb/xconnect.c:422
0m7.622s    0m2.388s  0m1.251s  busybox-1.18.5/util-linux/volume_id/jfs.c:48
0m8.761s    0m5.270s  0m1.369s  busybox-1.18.5/coreutils/test.c:286
0m13.863s   0m2.669s  0m1.261s  busybox-1.18.5/libbb/signals.c:23
0m16.563s   0m2.573s  0m1.245s  busybox-1.18.5/networking/libiproute/ll_addr.c:18
0m7.999s    0m3.653s  0m1.310s  busybox-1.18.5/coreutils/date.c:30
0m14.420s   0m2.322s  0m1.260s  busybox-1.18.5/miscutils/volname.c:19
0m9.570s    0m6.242s  0m1.405s  busybox-1.18.5/procps/top.c:999
0m13.556s   0m2.336s  0m1.247s  busybox-1.18.5/libbb/device_open.c:28
0m7.266s    0m2.816s  0m1.284s  busybox-1.18.5/networking/vconfig.c:143
0m7.258s    0m2.814s  0m1.277s  busybox-1.18.5/console-tools/openvt.c:8
0m8.238s    0m2.523s  0m1.244s  busybox-1.18.5/util-linux/volume_id/linux_swap.c:5
0m14.246s   0m2.202s  0m1.244s  busybox-1.18.5/libbb/change_identity.c:27
0m8.171s    0m2.814s  0m1.306s  busybox-1.18.5/selinux/sestatus.c:174
0m15.779s   0m2.466s  0m1.245s  busybox-1.18.5/coreutils/fsync.c:42
0m14.840s   0m1.382s  0m1.255s  busybox-1.18.5/libbb/rtc.c:53
0m7.779s    0m5.288s  0m1.355s  busybox-1.18.5/libbb/dump.c:759
0m14.686s   0m2.900s  0m1.292s  busybox-1.18.5/libbb/inet_common.c:178
0m7.350s    0m3.395s  0m1.301s  busybox-1.18.5/coreutils/tr.c:91
0m14.319s   0m2.413s  0m1.251s  busybox-1.18.5/coreutils/logname.c:1
0m14.237s   0m2.148s  0m1.272s  busybox-1.18.5/console-tools/chvt.c:10
```
