package IdleR_API;

// Future Directions:
//      -finish & implement 'lock' property
//      -finish & implement 'Upgrade' class

public class IdleR {
    //empty class
}

class Resource {
    private String name;
    private String desc;
    private int max;        // maximum quantity of resource
    private int min;        // minimum quantity of resource
    private int quant;      // actual quantity of resource
    private boolean lock;   //-NOT IMPLEMENTED- lock/unlock resource availability
    private boolean hasMax = false;

    //SETTERS
    public void setName(String name) {
        this.name = name;
    }
    public void setDesc(String desc) {
        this.desc = desc;
    }
    public void setMax(int max) {
        if (max > min) {
            hasMax = true;  // assigning max ALSO sets hasMax to true!!
            this.max = max;
        }
        else {
            System.err.println(name + "'s max cannot be lower than min");
        }
    }
    public void setMin(int min) {
        if (hasMax = true) {
            if (max < min) {
                System.err.println(name + "'s min cannot be higher than max");
            }
        }
        else {
            this.min = min;
        }
    }
    public void setQuant(int quant) {
        if (hasMax = true) {
            if (quant > max) {
                this.quant = max;
            }
        }
        if (quant < min) {
            this.quant = min;
        }
        else {
            this.quant = quant;
        }
    }
    public void setLock(boolean lock) {
        this.lock = lock;
    }
    // note: don't forget to assign value to max!!
    public void setMax(boolean hasMax) {
        this.hasMax = hasMax;
    }

    //GETTERS
    public String getName() {
        return name;
    }
    public String getDesc() {
        return desc;
    }
    public int getMax() {
        return max;
    }
    public int getMin() {
        return min;
    }
    public int getQuant() {
        return quant;
    }
    public boolean isLock() {
        return lock;
    }
    public boolean isMax() {
        return hasMax;
    }
    
    //CONSTRUCTOR
    
    public Resource (String n, boolean l, boolean m) {
        name = n;
        desc = "";
        min = 0;
        hasMax = m;
        quant = 0;
        lock = l;
    }

    //other METHODS
    
    public void modQuant(int amt) {
        quant += amt;
        //this originally had a logic structure to account for max/min values,
        //      but it wasn't working properly & was deleted
        //could setQuant be called here??? TEST THIS 3/29/21
    }
}



class Factor {
    //factor is short for 'factory'
    //factors produce and/or consume resources
    //factors can be purchased and sold using resources
    private String name;
    private String desc;
    private Delta[] cost;     // cost to build/buy factor
    private Delta[] produce;
    private Delta[] consume = {new Delta(new Resource("null", false, false), 0)};
    private int quant;        // number of factors possessed by player
    private double sellFactor;// proportion of buy cost recieved when sold
    private boolean hasProd;
    private boolean hasCon;
    private boolean canSell;
    private boolean lock;
    
    //SETTERS
    public void setName(String name) {
        this.name = name;
    }
    public void setDesc(String desc) {
        this.desc = desc;
    }
    public void setCost(Delta[] cost) {
        this.cost = cost;
    }
    public void setProduce(Delta[] produce) {
        hasProd = true;
        this.produce = produce;
    }
    public void setConsume(Delta[] consume) {
        hasCon = true;
        this.consume = consume;
    }
    public void setQuant(int quant) {
        this.quant = quant;
    }
    public void setSellFactor(double sellFactor) {
        this.sellFactor = sellFactor;
    }
    // don't forget to assign values to produce/consume if setting to true!!
    public void setHasProd(boolean hasProd) {
        this.hasProd = hasProd;
    }
    public void setHasCon(boolean hasCon) {
        this.hasCon = hasCon;
    }
    public void setCanSell(boolean canSell) {
        this.canSell = canSell;
    }
    public void setLock(boolean lock) {
        this.lock = lock;
    }
    
    //GETTERS
    public String getName() {
        return name;
    }
    public String getDesc() {
        return desc;
    }
    public Delta[] getCost() {
        return cost;
    }
    public Delta[] getProduce() {
        return produce;
    }
    public Delta[] getConsume() {
        return consume;
    }
    public int getQuant() {
        return quant;
    }
    public double getSellFactor() {
        return sellFactor;
    }
    public boolean isProd() {
        return hasProd;
    }
    public boolean isCon() {
        return hasCon;
    }
    public boolean isLock() {
        return lock;
    }
    
    //CONSTRUCTOR
    public Factor(String name, Delta[] cost, boolean canSell, boolean lock) {    
        this.name = name;
        this.desc = "";
        this.cost = cost;
        this.quant = 0;
        this.sellFactor = 0.5;  //by default, factors sell for half price 
        this.hasCon = false;
        this.hasProd = false;
        this.canSell = canSell;
        this.lock = lock;
    }

    //other METHODS
    
    //  NOTE: getCostInt() and getCostString() are set up to return values
    //      for the first item in Delta[] cost.  This was to save time when
    //      I was trying to complete this assignment.  More substantial code
    //      is required for 'cost' arrays of more than ONE element 
    public int getCostInt() {
        int c;
        c = cost[0].quant;
        return c;
    }
    
    public String getCostString() {
        String s;
        s = cost[0].rec.getName();
        return s;
    }
    
    public void convert() {
        //this method is the heart of the 'factor' class
        //Overview:
        //  -if factor is a consumer, check if there is enough
        //      of each resource consumed
        //      & if there is, reduce the quantity of each resource as needed
        //  -if factor is a producer
        //      (and there was sufficient consumed resources, if necessary)
        //      increase each resource quantity as needed
        
        //NOTE that the entire method is contained in a for-loop
        //this way, if there are multiple factors owned but only enough
        //  consumed resources for some of them, they can be allowed to convert
        
        boolean canAfford;
        int goCount;    //think like a space mission "go/no go"
        
        for (int i = 0; i < quant; i++) {
            canAfford = true;
            //first conditional is only for factors that CONSUME
            if (hasCon == true) {
                canAfford = false;
                goCount = 0;
                for (Delta delta : consume) {
                    //if the total quantity of resource is >= the consumed quantity...
                    if (delta.rec.getQuant() >= delta.quant){
                        //...add a virtual checkmark
                        goCount++;
                    }
                }
                //if all consumed resources are 'go'...
                if(goCount == consume.length) {
                    //...note via canAfford & deduct consumed cost from rec.quant
                    canAfford = true;
                    for (Delta delta : consume) {
                        delta.rec.modQuant(-delta.quant);
                    }
                }
            }
            //second conditional is only for factors that PRODUCE && can afford to
            if (canAfford && hasProd) {
                for (Delta delta : produce) {
                        delta.rec.modQuant(delta.quant);
                    }
            }
        }
        
        
    }
    public void buy(int amt){
        int goCount;
        
        //you can only buy a positive amount!
        if (amt > 0) {
            goCount = 0;
            for (Delta cost1 : cost) {
                // if the cost is less than the amount of Resource needed...
                if (cost1.rec.getQuant() >= (cost1.quant * amt)) {
                    // ...indicate sufficient quantity
                    goCount++;
                }
            }
            // if there are sufficient quantities of ALL resources
            //      needed to buy, enter purchase routine
            if(goCount == cost.length) {
                // First, modify consumed resources
                for (Delta cost1 : cost) {
                    cost1.rec.modQuant(-(cost1.quant * amt));
                }
                // Then, modify Factor quantity
                quant += amt;
            }
        }
    }
    public void sell(int amt) {
        // Can't sell what you don't have && can't sell what isn't sellable!
        if (amt <= quant && canSell == true) {
            // First, modify consumed resources
            for (Delta cost1 : cost) {
                cost1.rec.modQuant((int) (cost1.quant * amt * sellFactor));
            }
            // Then, modify Factor quantity
            quant -= amt;
        }
    }
}


//Upgrades are a classic element of idle games, but I may skip this part to save time!
class Upgrade {
    private String name;
    private String desc;
    private int cost;
    private boolean lock;
    
    //SETTERS
    public void setName(String name) {
        this.name = name;
    }
    public void setDesc(String desc) {
        this.desc = desc;
    }
    public void setCost(int cost) {
        this.cost = cost;
    }
    public void setLock(boolean lock) {
        this.lock = lock;
    }
    
    //GETTERS
    public String getName() {
        return name;
    }
    public String getDesc() {
        return desc;
    }
    public int getCost() {
        return cost;
    }
    public boolean isLock() {
        return lock;
    }

    //CONSTRUCTOR
    public Upgrade(String name, String desc, int cost, boolean lock) {
        this.name = name;
        this.desc = desc;
        this.cost = cost;
        this.lock = lock;
    }
    
    //other METHODS
    
    //lock, unlock
}



class Delta {
    // this class represents cost as an object, separate from the resources themselves
    Resource rec;
    int quant;
    
    //GETTERS
    public Resource getRec() {
        return rec;
    }
    public int getQuant() {
        return quant;
    }

    //SETTERS
    public void setQuant(int quant) {
        this.quant = quant;
    }
    public void setRec(Resource rec) {
        this.rec = rec;
    }
    
    //CONSTRUCTOR
    public Delta(Resource rec, int quant) {
        this.rec = rec;
        this.quant = quant;
    }
}