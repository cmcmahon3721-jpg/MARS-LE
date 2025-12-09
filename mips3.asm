season $t1, $zero, 3
season $t3, $zero 2
season $t5, $zero 5
season $t6, $zero, 25

mix $t4, $t1, $t3
bake $t4, $t4, $t5

taste $t4, $t6, PLATE
throw_out $t4

PLATE:
plate $t7, $t4