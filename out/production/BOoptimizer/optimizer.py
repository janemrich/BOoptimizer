from subprocess import call
call(["java",
	"de/jan/Main",
	"8", # Threads
	"false", # multi
	"true", # add delete
	"false", # single point
	"false", # retain
	"8",	# population size
	"false", # random
	"true", # converge
	"0.9",	# converge threshold in 0 - 100 percent
	"4096", # terminate after # evals
	"/home/jan/Documents/Starcraft/test/converge", # Log directory
	"/home/jan/StarCraftII/Versions/Base59877/SC2_x64", # SC2 location
	"/home/jan/Documents/Starcraft/commandcenter/bin/CommandCenter"]) # Bot location
