from subprocess import call
call(["java",
	"de/jan/Main",
	"8", # Threads
	"false", # multi
	"true", # add delete
	"false", # single point
	"true", # retain
	"32",	# population size
	"false", # random
	"1024",	# tests
	"/home/jan/Documents/Starcraft/Log", # Log directory
	"/home/jan/StarCraftII/Versions/Base59877/SC2_x64", # SC2 location
	"/home/jan/Documents/Starcraft/commandcenter/bin/CommandCenter"]) # Bot location
