	.data
newline:	.asciiz "\n"
	.text
	.globl main
main:
	li $fp, 0x7ffffffc

B1:

	# loadI 0 => r_INPUT
	li $t0, 0
	sw $t0, 0($fp)

	# loadI 0 => r_ISPRIME
	li $t0, 0
	sw $t0, -4($fp)

	# loadI 0 => r_DIVISOR
	li $t0, 0
	sw $t0, -8($fp)

	# readInt  => r_INPUT
	li $v0, 5
	syscall
	add $t0, $v0, $zero
	sw $t0, 0($fp)

	# loadI 1 => r2
	li $t0, 1
	sw $t0, -16($fp)

	# i2i r2 => r_ISPRIME
	lw $t1, -16($fp)
	add $t0, $t1, $zero
	sw $t0, -4($fp)

	# jumpI =&gt; B2
	j B2

B2:

	# loadI 2 => r2
	li $t0, 2
	sw $t0, -16($fp)

	# slt r_INPUT, r2 => r1
	lw $t1, 0($fp)
	lw $t2, -16($fp)
	slt $t1 , $t1, $t2
	sw $t1, -12($fp)

	# cbr r1 -> B3, B4
	lw $t0, -12($fp)
	beq $t0, $zero, B4

	# loadI 0 => r2
	li $t0, 0
	sw $t0, -16($fp)

	# i2i r2 => r_ISPRIME
	lw $t1, -16($fp)
	add $t0, $t1, $zero
	sw $t0, -4($fp)

	# jumpI => B5
	j B5
B4:

	# loadI 1 => r2
	li $t0, 1
	sw $t0, -16($fp)

	# sub r_INPUT, r2 => r2
	lw $t1, 0($fp)
	lw $t2, -16($fp)
	sub $t2 , $t1, $t2
	sw $t2, -16($fp)

	# i2i r2 => r_DIVISOR
	lw $t1, -16($fp)
	add $t0, $t1, $zero
	sw $t0, -8($fp)

	# jumpI => B6
	j B6

B6:

	# loadI 2 => r2
	li $t0, 2
	sw $t0, -16($fp)

	# sge r_DIVISOR, r2 => r1
	lw $t1, -8($fp)
	lw $t2, -16($fp)
	sge $t1 , $t1, $t2
	sw $t1, -12($fp)

	# cbr r1 -> B7, B8
	lw $t0, -12($fp)
	beq $t0, $zero, B8

	# jumpI =&gt; B9
	j B9

B9:

	# rem r_INPUT, r_DIVISOR => r1
	lw $t1, 0($fp)
	lw $t2, -8($fp)
	rem $t1 , $t1, $t2
	sw $t1, -12($fp)

	# loadI 0 => r2
	li $t0, 0
	sw $t0, -16($fp)

	# seq r_r1, r2 => r1
	lw $t1, -12($fp)
	lw $t2, -16($fp)
	seq $t1 , $t1, $t2
	sw $t1, -12($fp)

	# cbr r1 -> B10, B11
	lw $t0, -12($fp)
	beq $t0, $zero, B11

	# loadI 0 => r2
	li $t0, 0
	sw $t0, -16($fp)

	# i2i r2 => r_ISPRIME
	lw $t1, -16($fp)
	add $t0, $t1, $zero
	sw $t0, -4($fp)

	# jumpI => B12
	j B12
B11:

B12:

	# loadI 1 => r2
	li $t0, 1
	sw $t0, -16($fp)

	# sub r_DIVISOR, r2 => r2
	lw $t1, -8($fp)
	lw $t2, -16($fp)
	sub $t2 , $t1, $t2
	sw $t2, -16($fp)

	# i2i r2 => r_DIVISOR
	lw $t1, -16($fp)
	add $t0, $t1, $zero
	sw $t0, -8($fp)

	# jumpI => B6
	j B6

B8:

B5:

	# jumpI =&gt; B13
	j B13

B13:

	# cbr r1 -> B14, B15
	lw $t0, -12($fp)
	beq $t0, $zero, B15

	# loadI 1 => r1
	li $t0, 1
	sw $t0, -12($fp)

	# writeInt r1
	li $v0, 1
	lw $t1, -12($fp)
	add $a0, $t1, $zero
	syscall
	li $v0, 4
	la $a0, newline
	syscall

	# jumpI => B16
	j B16
B15:

	# loadI 0 => r1
	li $t0, 0
	sw $t0, -12($fp)

	# writeInt r1
	li $v0, 1
	lw $t1, -12($fp)
	add $a0, $t1, $zero
	syscall
	li $v0, 4
	la $a0, newline
	syscall

B16:

	# exit
	li $v0, 10
	syscall

