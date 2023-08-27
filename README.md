# BOoptimizer

This is my bachelor thesis project. It optimizes Starcraft II build orders with a genetic algorithm and the actual game engine.

The genetic algorithm is implemented in Java. It tests the fitness of the individuals with multi-threaded call to the C++ Commandcenter Bot, which executes the build order in a connected game running on the real StarCraft II game engine.

The game is controlled via an adapted CommandCenterbot here https://github.com/janemrich/commandcenter
