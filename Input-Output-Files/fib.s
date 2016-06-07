	.data
newline:	.asciiz "\n"
	.text
	.globl main
main:
	li $fp, 0x7ffffffc

B1:

	# loadI 0 => r_LAST
	li $t0, 0
	sw $t0, 0($fp)

	# loadI 0 => r_NEXTTOLAST
	li $t0, 0
	sw $t0, -4($fp)

	# loadI 0 => r_LIMIT
	li $t0, 0
	sw $t0, -8($fp)

	# loadI 0 => r_FIB
	li $t0, 0
	sw $t0, -12($fp)

	# readInt  => r_LIMIT
	li $v0, 5
	syscall
	add $t0, $v0, $zero
	sw $t0, -8($fp)

	# loadI 1 => r2
	li $t0, 1
	sw $t0, -20($fp)

	# i2i r2 => r_LAST
	lw $t1, -20($fp)
	add $t0, $t1, $zero
	sw $t0, 0($fp)

	# loadI 0 => r2
	li $t0, 0
	sw $t0, -20($fp)

	# i2i r2 => r_NEXTTOLAST
	lw $t1, -20($fp)
	add $t0, $t1, $zero
	sw $t0, -4($fp)

	# loadI 1 => r2
	li $t0, 1
	sw $t0, -20($fp)

	# i2i r2 => r_FIB
	lw $t1, -20($fp)
	add $t0, $t1, $zero
	sw $t0, -12($fp)

	# loadI 0 => r1
	li $t0, 0
	sw $t0, -16($fp)

	# writeInt r1
	li $v0, 1
	lw $t1, -16($fp)
	add $a0, $t1, $zero
	syscall
	li $v0, 4
	la $a0, newline
	syscall

	# jumpI => B2
	j B2

B2:

	# sle r_FIB, r_LIMIT => r1
	lw $t1, -12($fp)
	lw $t2, -8($fp)
	sle $t1 , $t1, $t2
	sw $t1, -16($fp)

	# cbr r1 -> B3, B4
	lw $t0, -16($fp)
	beq $t0, $zero, B4

	# writeInt r_FIB
	li $v0, 1
	lw $t1, -12($fp)
	add $a0, $t1, $zero
	syscall
	li $v0, 4
	la $a0, newline
	syscall

	# add r_LAST, r_NEXTTOLAST => r2
	lw $t1, 0($fp)
	lw $t2, -4($fp)
	add $t2 , $t1, $t2
	sw $t2, -20($fp)

	# i2i r2 => r_FIB
	lw $t1, -20($fp)
	add $t0, $t1, $zero
	sw $t0, -12($fp)

	# i2i r_LAST => r_NEXTTOLAST
	lw $t1, 0($fp)
	add $t0, $t1, $zero
	sw $t0, -4($fp)

	# i2i r_FIB => r_LAST
	lw $t1, -12($fp)
	add $t0, $t1, $zero
	sw $t0, 0($fp)

	# jumpI => B2
	j B2

B4:

	# exit
	li $v0, 10
	syscall

