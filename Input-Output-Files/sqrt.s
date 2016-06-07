	.data
newline:	.asciiz "\n"
	.text
	.globl main
main:
	li $fp, 0x7ffffffc

B1:

	# loadI 0 => r_N
	li $t0, 0
	sw $t0, 0($fp)

	# loadI 0 => r_SQRT
	li $t0, 0
	sw $t0, -4($fp)

	# readInt  => r_N
	li $v0, 5
	syscall
	add $t0, $v0, $zero
	sw $t0, 0($fp)

	# loadI 0 => r2
	li $t0, 0
	sw $t0, -12($fp)

	# i2i r2 => r_SQRT
	lw $t1, -12($fp)
	add $t0, $t1, $zero
	sw $t0, -4($fp)

	# jumpI => B2
	j B2

B2:

	# mul r_SQRT, r_SQRT => r1
	lw $t1, -4($fp)
	lw $t2, -4($fp)
	mul $t1 , $t1, $t2
	sw $t1, -8($fp)

	# sle r1, r_N => r1
	lw $t1, -8($fp)
	lw $t2, 0($fp)
	sle $t1 , $t1, $t2
	sw $t1, -8($fp)

	# cbr r1 -> B3, B4
	lw $t0, -8($fp)
	beq $t0, $zero, B4

	# loadI 1 => r2
	li $t0, 1
	sw $t0, -12($fp)

	# add r_SQRT, r2 => r2
	lw $t1, -4($fp)
	lw $t2, -12($fp)
	add $t2 , $t1, $t2
	sw $t2, -12($fp)

	# i2i r2 => r_SQRT
	lw $t1, -12($fp)
	add $t0, $t1, $zero
	sw $t0, -4($fp)

	# jumpI => B2
	j B2

B4:

	# loadI 1 => r2
	li $t0, 1
	sw $t0, -12($fp)

	# sub r_SQRT, r2 => r2
	lw $t1, -4($fp)
	lw $t2, -12($fp)
	sub $t2 , $t1, $t2
	sw $t2, -12($fp)

	# i2i r2 => r_SQRT
	lw $t1, -12($fp)
	add $t0, $t1, $zero
	sw $t0, -4($fp)

	# writeInt r_SQRT
	li $v0, 1
	lw $t1, -4($fp)
	add $a0, $t1, $zero
	syscall
	li $v0, 4
	la $a0, newline
	syscall

	# exit
	li $v0, 10
	syscall

