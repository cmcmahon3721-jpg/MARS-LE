season $t0, $zero, 10
season $t1, $zero, 5
season $t2, $zero, 3

mix $t0, $t0, $t1
blend $t0, $t0, $t1
salt $t0
pepper $t0

spill $t0, $t0, $t2
