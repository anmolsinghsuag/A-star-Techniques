import java.util.*;

public class Knight {

    private int expanded;
    private int moves;

    public static void main(String[] args) {
        //Testing for 20 random problems
        Knight knight = new Knight();
        int runs=20;
        for(int i =1;i<=runs;i++){
            int x = (int )(Math.random() * 20 + 1);
            int y = (int )(Math.random() * 10 + 1);
            State goalState = new State(x,y);
            Long startTime = System.currentTimeMillis();
            knight.problem(goalState);
            Long endTime = System.currentTimeMillis();

            //System.out.println(i+","+knight.moves+","+knight.expanded+","+(endTime-startTime));
            System.out.println("Problem Number :"+i+" ,Optimal Moves : "+knight.moves+" ,Nodes Expanded : "+knight.expanded+" ,Time (ms) : "+(endTime-startTime));


        }

    }


    //Creates a Knight Problem with initial position as 0,0
    public void problem(State goalState){
        moves=0;
        expanded=0;
        State startState = new State(0,0);
        Node root = new Node(goalState,startState,null,0.0);

        PriorityQueue<Node> frontier = new PriorityQueue<Node>(new Comparator<Node>() {
            @Override
            public int compare(Node o1, Node o2) {
                if (o1.f_cost() > o2.f_cost()){
                    return 1;
                }
                else if(o1.f_cost() < o2.f_cost()){
                    return -1;
                }
                else return 0;
            }
        });

        Map<State,Node> explored = new HashMap<State,Node>();
        Map<State,Node> frontierMap = new HashMap<State,Node>();


        frontier.offer(root);
        frontierMap.put(root.getState(),root);


        Node solution = findSolution(frontier,goalState,explored,frontierMap);
        if(solution!=null){
            //System.out.println("Optimal Path");
            printPath(solution,root);

        }
        else System.out.println("Failure");
    }

    //Finds Solution the the knight problem
    public Node findSolution(PriorityQueue<Node> frontier, State goalState,Map<State,Node> explored,Map<State,Node> frontierMap){
        while (!frontier.isEmpty()){
            Node current = frontier.poll();
            frontierMap.remove(current.getState());
            if (current.getState().isGoalState(goalState)){
                return current;
            }
            else{
                expanded++;
                //System.out.println("Expanded position"+current.getState().getX()+" , "+current.getState().getY());
                explored.put(current.getState(),current);
                List<Node> children = current.getChildren();

                for(Node child:children){
                    if(!explored.containsKey(child.getState()) && !frontierMap.containsKey(child.getState())){
                        frontier.offer(child);
                        frontierMap.put(child.getState(),child);
                        //System.out.println("Adding child"+child.getState().getX()+" , "+child.getState().getY());
                    }
                    else if(frontierMap.containsKey(child.getState()) && frontierMap.get(child.getState()).f_cost()>child.f_cost()){
                        frontier.remove(frontierMap.get(child.getState()));
                        frontier.add(child);
                        frontierMap.put(child.getState(),child);
                    }
                }

            }
        }

        return null;
    }

    //Prints the path
    public void printPath(Node solution, Node initial){
        if(solution==null) return;
        printPath(solution.getParent(),initial);
        //System.out.println("X : "+solution.getState().getX()+" , Y : "+solution.getState().getY());
        moves++;
    }


}

//Node Class
class Node {
    private Double g_cost;
    private Double h_cost;
    private Double f_cost;
    private Node parent;
    private State state;
    private State goalState;

    public Node(State goalState, State state,Node parent,Double g_cost) {
        this.h_cost = calculateHeuristic(goalState,state);
        this.state = state;
        this.parent = parent;
        this.g_cost = g_cost;
        this.f_cost = f_cost();
        this.goalState = goalState;

    }

    public List<Node> getChildren(){
        List<Node> children = new ArrayList<Node>();
        State s1 = new State(this.getState().getX()+2,this.getState().getY()+1);
        State s2 = new State(this.getState().getX()+2,this.getState().getY()-1);
        State s3 = new State(this.getState().getX()-2,this.getState().getY()+1);
        State s4 = new State(this.getState().getX()-2,this.getState().getY()-1);
        State s5 = new State(this.getState().getX()+1,this.getState().getY()+2);
        State s6 = new State(this.getState().getX()-1,this.getState().getY()+2);
        State s7 = new State(this.getState().getX()+1,this.getState().getY()-2);
        State s8 = new State(this.getState().getX()-1,this.getState().getY()-2);

        children.add(new Node(this.goalState,s1,this,this.g_cost+1));
        children.add(new Node(this.goalState,s2,this,this.g_cost+1));
        children.add(new Node(this.goalState,s3,this,this.g_cost+1));
        children.add(new Node(this.goalState,s4,this,this.g_cost+1));
        children.add(new Node(this.goalState,s5,this,this.g_cost+1));
        children.add(new Node(this.goalState,s6,this,this.g_cost+1));
        children.add(new Node(this.goalState,s7,this,this.g_cost+1));
        children.add(new Node(this.goalState,s8,this,this.g_cost+1));
        return children;




    }

    public Double calculateHeuristic(State goalState,State state){

        //return  Math.sqrt(Math.pow(goalState.getX() -state.getX(),2) + Math.pow(goalState.getY() -state.getY(),2));
        double manhattan = Math.abs(goalState.getX()-state.getX())+Math.abs(goalState.getY()-state.getY());
        double h = Math.ceil(manhattan/3);
        return h;
    }

    public Double f_cost(){
        return this.g_cost + this.h_cost;
    }

    public Double getF_cost() {
        return f_cost;
    }

    public void setF_cost(Double f_cost) {
        this.f_cost = f_cost;
    }

    public Double getG_cost() {
        return g_cost;
    }

    public void setG_cost(Double g_cost) {
        this.g_cost = g_cost;
    }

    public Double getH_cost() {
        return h_cost;
    }

    public void setH_cost(Double h_cost) {
        this.h_cost = h_cost;
    }

    public Node getParent() {
        return parent;
    }

    public void setParent(Node parent) {
        this.parent = parent;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }
}
// State Class
class State {
    private int x;
    private int y;

    public State(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public boolean isGoalState(State goal){
        return goal.getX() == this.x && goal.getY() == this.y;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof State)){
            return false;
        }

        State other_ = (State) other;

        return other_.x == this.x && other_.y == this.y;
    }

    @Override
    public int hashCode() {
        return 31 * Objects.hashCode(this.x) + Objects.hashCode(this.y);
    }
}


