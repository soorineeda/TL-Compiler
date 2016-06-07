	.data
newline:	.asciiz "\n"
	.text
	.globl main
main:
	li $fp, 0x7ffffffc

B1:

	# loadI 0 => r_X
	li $t0, 0
	sw $t0, 0($fp)

	# loadI 10 => r2
	li $t0, 10
	sw $t0, -8($fp)

	# i2i r2 => r_X
	lw $t1, -8($fp)
	add $t0, $t1, $zero
	sw $t0, 0($fp)

	# loadI 1 => r2
	li $t0, 1
	sw $t0, -8($fp)

	# i2i r2 => r_X
	lw $t1, -8($fp)
	add $t0, $t1, $zero
	sw $t0, 0($fp)

	# loadI 2 => r1
	li $t0, 2
	sw $t0, -4($fp)

	# loadI 6 => r1
	li $t0, 6
	sw $t0, -4($fp)

	# loadI 2 => r2
	li $t0, 2
	sw $t0, -8($fp)

	# sub r_X, r2 => r2
	lw $t1, 0($fp)
	lw $t2, -8($fp)
	sub $t2 , $t1, $t2
	sw $t2, -8($fp)

	# div r1, r_r2 => r2
	lw $t1, -4($fp)
	lw $t2, -8($fp)
	div $t2 , $t1, $t2
	sw $t2, -8($fp)

	# mul r_X, r2 => r2
	lw $t1, 0($fp)
	lw $t2, -8($fp)
	mul $t2 , $t1, $t2
	sw $t2, -8($fp)

	# add r1, r_r2 => r1
	lw $t1, -4($fp)
	lw $t2, -8($fp)
	add $t1 , $t1, $t2
	sw $t1, -4($fp)

	# loadI 4 => r2
	li $t0, 4
	sw $t0, -8($fp)

	# rem r_r1, r2 => r2
	lw $t1, -4($fp)
	lw $t2, -8($fp)
	rem $t2 , $t1, $t2
	sw $t2, -8($fp)

	# i2i r2 => r_X
	lw $t1, -8($fp)
	add $t0, $t1, $zero
	sw $t0, 0($fp)

	# exit
	li $v0, 10
	syscall

