# Chess-AI-Practice
A good environment to practice MiniMax and Alpha-Beta-Pruning

## Pair Programming
We want you to use pair programming for this project. Find someone next to you to team up with.

One of you will be the "driver" (the person who writes the code), and the other person is the "observer", reviewing each line as it is typed and providing feedback if something doesn't look right or if they don't understand what the driver is doing.

You should switch driver and observer when you finish DFS and start BFS, to give both of you equal practice with both rolls, as well as writing graph searching code.

You can read more about pair programming [here.](https://en.wikipedia.org/wiki/Pair_programming)

## Slides
Here are the slides I used. They contain pseudocode, if you don't know where to start, it's here. 

[MiniMax](https://docs.google.com/presentation/d/1rytUj8KX6VS1dtYSCM9JOWBLQWFK-GF5yfmocGmXNT0/edit?usp=sharing)

[Alpha-Beta Pruning](https://docs.google.com/presentation/d/1VRX5_HVNtHFApuWscfXN7uZ7udZq8vWyvenLz-gtONw/edit?usp=sharing)

## Installing
**To get this on your computer:**

1) Clone the repository onto your computer. If you don't know how to do this, open up the terminal and type:

```bash
cd sigai
git clone https://github.com/PurdueSIGAI/Chess-AI-Practice.git
```

2) Open up IntelliJ

3) Do File -> Open, select the folder "Chess-AI-Practice" where you cloned it (in the "sigai" folder if you executed the above).

## Testing
You can run the program by executing ^r or doing Run -> Run.

When you run the program, you can specify a white player and a black player. At least one of these should be the AI you are testing.

There are several AI's already in the program, you can run yours against theirs to see how good it is.

## Your task

###Week 1
Your task is to complete the MiniMax algorithm in MiniMaxAI.java in the 'you' package.

###Week 2
Your task is to complete the Alpha Beta Pruning algroithm in AlphaBetaPruningAI.java in the 'you' package.

###Extra Task
If you have extra time in either week 1 or week 2, try to make improvements to the Evaluation function. If you don't know where to begin, [here](https://chessprogramming.wikispaces.com/Evaluation) are some ideas.
## The API
####All of the classes you need to worry about are in the 'you' package.

####The recursive function you implement will have four parameters:
* What ply (number of moves into the future) you're currently on.
* What ply you should finish on
* An object representing the board
* An enum value representing the currenty player to-move

####To evaluate a board:
* Evaluation.evaluateBoard(board, side)

####To get a list of all possible moves:
* board.allMoves(side, false)

####Perform a move on the board:
* board.move(move)

####Undo a move on the board:
* board.undo()

####MoveScore may also be useful as an object to return. It contains two fields:
* The score for if you take that move
* The move itself
