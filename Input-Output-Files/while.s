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

	# jumpI => B2
	j B2

B2:

	# loadI 10 => r2
	li $t0, 10
	sw $t0, -8($fp)

	# sle r_X, r2 => r1
	lw $t1, 0($fp)
	lw $t2, -8($fp)
	sle $t1 , $t1, $t2
	sw $t1, -4($fp)

	# cbr r1 -> B3, B4
	lw $t0, -4($fp)
	beq $t0, $zero, B4

	# jumpI => B5
	j B5

B5:

	# loadI 8 => r2
	li $t0, 8
	sw $t0, -8($fp)

	# sge r_X, r2 => r1
	lw $t1, 0($fp)
	lw $t2, -8($fp)
	sge $t1 , $t1, $t2
	sw $t1, -4($fp)

	# cbr r1 -> B6, B7
	lw $t0, -4($fp)
	beq $t0, $zero, B7

	# loadI 2 => r2
	li $t0, 2
	sw $t0, -8($fp)

	# add r_X, r2 => r2
	lw $t1, 0($fp)
	lw $t2, -8($fp)
	add $t2 , $t1, $t2
	sw $t2, -8($fp)

	# i2i r2 => r_X
	lw $t1, -8($fp)
	add $t0, $t1, $zero
	sw $t0, 0($fp)

	# jumpI => B5
	j B5

B7:

	# jumpI => B2
	j B2

B4:

	# exit
	li $v0, 10
	syscall

