import java.util.*;

public class TSP {
    private int num_nodes_expanded;

    public static void main(String[] args) {
        //Testing code for 3 instances each for 3 to 18 cities
        double eps = 0.1;
        TSP problem = new TSP();

        int maxCity = 18;
        for(int i = 3; i <= maxCity;i++){
            problem.problemInstance(i,eps);
            problem.problemInstance(i,eps);
            problem.problemInstance(i,eps);
        }

    }

    //Creates a random city map and runs A*
    public void problemInstance(int cityCount,double eps){
        Node root = generateProblem(cityCount,eps);
        this.num_nodes_expanded=0;

        PriorityQueue<Node> frontier = new PriorityQueue<Node>(new Comparator<Node>() {
            @Override
            public int compare(Node o1, Node o2) {
                if (o1.getF_cost() > o2.getF_cost()){
                    return 1;
                }
                else if(o1.getF_cost() < o2.getF_cost()){
                    return -1;
                }
                else return 0;
            }
        });

        Map<List<Integer>,Node> explored = new HashMap<List<Integer>,Node>();
        Map<List<Integer>,Node> frontierMap = new HashMap<List<Integer>,Node>();

        double root_h = calculateHeuristic(root,cityCount);
        root.setH_cost(root_h);

        frontier.offer(root);
        frontierMap.put(root.getState(),root);
        Long startTime = System.currentTimeMillis();
        Node solution = findSolution(explored,frontier,frontierMap,cityCount);
        Long endTime = System.currentTimeMillis();

        //printMap(root.getMap(),cityCount);
        System.out.println("Solution for City Count = "+cityCount);
        System.out.print("Travelling Salesman Path = ");
        if(solution!=null){
            for(Integer i:solution.getState()){
                System.out.print(i+" ");
            }
            System.out.println("");
        }
        System.out.println("Number of Nodes expanded = "+this.num_nodes_expanded);
        System.out.println("Execution Time = "+(endTime-startTime));
        System.out.println("");
    }

    //Calculates the Heuristic
    public Double calculateHeuristic(Node node,int cityCount){
        double mst = calculateMST(node,cityCount);
        double min_to_root = calculateMinToRoot(node,cityCount);
        double min_to_child = calculateMinToChild(node,cityCount);

        return mst+min_to_root+min_to_child;
    }

    public Double calculateMinToChild(Node node,int cityCount){
        if(node.getState().size()>=cityCount){
            return 0.0;
        }
        double min_to_child = Double.MAX_VALUE;
        for(int i=0;i<cityCount;i++){
            if(!node.getState().contains(i) && node.getMap()[node.getCityId()][i]<min_to_child ){
                min_to_child = node.getMap()[node.getCityId()][i];
            }
        }
        return min_to_child;

    }

    public Double calculateMinToRoot(Node node,int cityCount){
        if(node.getState().size()>=cityCount){
            return node.getMap()[node.getCityId()][0];
        }
        double min_to_root = Double.MAX_VALUE;
        for(int i=0;i<cityCount;i++){
            if(!node.getState().contains(i) && node.getMap()[i][0]<min_to_root ){
                min_to_root = node.getMap()[i][0];
            }
        }
        return min_to_root;
    }

    //Runs A*
    public Node findSolution(Map<List<Integer>,Node> explored,PriorityQueue<Node> frontier,Map<List<Integer>,Node> frontierMap,int cityCount){
        while (!frontier.isEmpty()){
            Node current = frontier.poll();
            frontierMap.remove(current.getState());

            //System.out.println("Explored City "+current.getCityId()+" F : "+current.getF_cost()+" G : "+current.getG_cost()+" H : "+current.getH_cost()+" L:"+current.getState().toString());
            if(current.getState().size()==cityCount+1 && current.getState().get(0)==0 && current.getState().get(cityCount)==0){
                return current;
            }
            else {
                explored.put(current.getState(),current);
                this.num_nodes_expanded++;
                List<Node> children = getChildren(current,cityCount);
                for(Node child:children){
                    if(!explored.containsKey(child.getState()) && !frontierMap.containsKey(child.getState())){
                        frontier.offer(child);
                        frontierMap.put(child.getState(),child);
                        //System.out.println("Adding child"+child.getState().getX()+" , "+child.getState().getY());
                    }
                    else if(frontierMap.containsKey(child.getState()) && frontierMap.get(child.getState()).getF_cost()>child.getF_cost()){
                        frontier.remove(frontierMap.get(child.getState()));
                        frontier.add(child);
                        frontierMap.put(child.getState(),child);
                    }
                }
            }
        }

        return null;

    }


    //Returns children of a node
    public List<Node> getChildren(Node parent,Integer cityCount){
        List<Node> children = new ArrayList<Node>();
        for(int i=0;i<cityCount;i++){
            if(!parent.getState().contains(i)){
                List<Integer> childState=new ArrayList<>();
                childState.addAll(parent.getState());
                childState.add(i);
                Node child=new Node(parent.getG_cost()+parent.getMap()[parent.getCityId()][i],parent,i,parent.getMap(),childState);
                child.setH_cost(calculateHeuristic(child,cityCount));
                children.add(child);
            }
        }
        if (children.size()==0){
            List<Integer> goalState=new ArrayList<>();
            goalState.addAll(parent.getState());
            goalState.add(0);
            Node child=new Node(parent.getG_cost()+parent.getMap()[parent.getCityId()][0],parent,0,parent.getMap(),goalState);
            child.setH_cost(calculateHeuristic(child,cityCount));
            children.add(child);
        }
        return children;
    }


    //Returns sum of edges in a MST from a distance matrix, not including cities in removed set
    public double calculateMST(Node node,int cityCount){
        if(cityCount-node.getState().size()<2){
            return 0.0;
        }
        double cost = 0.0;

        List<Integer> visited = new ArrayList<>();

        int minCity1=-1;
        int minCity2=-1;
        double minDistance=Double.MAX_VALUE;

        //Find the first smallest edge
        for(int i =0;i<cityCount;i++){
            for (int j=0;j<cityCount;j++){
                if(!node.getState().contains(i) && !node.getState().contains(j) && i!=j){
                    if(node.getMap()[i][j]<minDistance){
                        minDistance = node.getMap()[i][j];
                        minCity1 = i;
                        minCity2 = j;
                    }
                }
            }
        }
        visited.add(minCity1);
        visited.add(minCity2);
        cost = cost + node.getMap()[minCity1][minCity2];
        //System.out.println("Added "+map[minCity1][minCity2]);

        //Find remaining edges
        while (visited.size()< (cityCount-node.getState().size())){
            int minC = -1;
            double minD = Double.MAX_VALUE;
            for(Integer visitedCity:visited){
                for (int i=0;i<cityCount;i++){
                    if(!node.getState().contains(i) && !visited.contains(i)){
                        if(node.getMap()[visitedCity][i]<minD){
                            minD = node.getMap()[visitedCity][i];
                            minC = i;
                        }
                    }
                }
            }
            visited.add(minC);
            cost = cost+minD;
            //System.out.println("Added "+minD);
        }
        return cost;
    }



    //Problem Generator : Generates cityCount cities in a unit square with minimum eps distance, returns starting node
    public Node generateProblem(int cityCount,Double eps){
        if(cityCount<1) return null;
        int curr = 0;

        double locations_x[] = new double[cityCount];
        double locations_y[] = new double[cityCount];
        double distances[][] = new double[cityCount][cityCount];

        //Generate random X,Y coordinates in [ 0 , 1 ]
        while (curr<cityCount){
            double x = Math.random();
            double y = Math.random();

            boolean flag = true;
            for(int a=0;a<curr;a++){
                double distance = Math.sqrt(Math.pow(locations_x[a] - x,2) + Math.pow(locations_y[a] - y,2));
                //Epsilon Check
                if (distance < eps){
                    flag = false;
                    break;
                }
            }

            if(flag){
                locations_x[curr] = x;
                locations_y[curr] = y;
                curr++;
            }
        }
        //Populate distances matrix
        for(int a = 0;a<cityCount;a++){
            for(int b = 0;b<cityCount;b++){
               distances[a][b] = Math.sqrt(Math.pow(locations_x[a]-locations_x[b],2)+Math.pow(locations_y[a]-locations_y[b],2));
            }
        }

        //Create root node
        List<Integer> rootState = new ArrayList<>();
        rootState.add(0);
        return new Node(0.0,null,0,distances,rootState);
    }


    //Pretty prints an distance matrix
    public void printMap(double[][]map,int cityCount){
        for(int i=0;i<cityCount;i++){
            for(int j =0;j<cityCount;j++){
                System.out.print(map[i][j]+" ");
            }
            System.out.println("");
        }
    }
}

//Node Class
class Node {

    private double g_cost;
    private double h_cost;
    private double f_cost;
    private Node parent;
    private int cityId;
    private double[][] map;
    private List<Integer> state;

    public Node(double g_cost, Node parent, int cityId, double[][] map,List<Integer> state) {
        this.g_cost = g_cost;
        this.parent = parent;
        this.cityId = cityId;
        this.map = map;
        this.state = state;
    }

    public List<Integer> getState() {
        return state;
    }

    public void setState(List<Integer> state) {
        this.state = state;
    }

    public double getG_cost() {
        return g_cost;
    }

    public void setG_cost(double g_cost) {
        this.g_cost = g_cost;
    }

    public double getH_cost() {
        return h_cost;
    }

    public void setH_cost(double h_cost) {
        this.h_cost = h_cost;
        setF_cost(this.g_cost+this.h_cost);
    }

    public double getF_cost() {
        return f_cost;
    }

    public void setF_cost(double f_cost) {
        this.f_cost = f_cost;
    }

    public Node getParent() {
        return parent;
    }

    public void setParent(Node parent) {
        this.parent = parent;
    }

    public int getCityId() {
        return cityId;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }

    public double[][] getMap() {
        return map;
    }

    public void setMap(double[][] map) {
        this.map = map;
    }
}

