#!/usr/bin/expect
set timeout -1

spawn ../../../challenge_program/bin/withmi -d ../../../challenge_program/data -s ../../../challenge_program/data -i ../../../examples/sally.id

expect {WithMi>}
send "\n"

expect "WithMi>"

expect "Never gonna happen"

