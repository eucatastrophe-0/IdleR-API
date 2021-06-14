// A widget is a hypothetical manufactured good.
// In this game, you manage the modest supply chain of a widget manufacturer.
// Expand your empire & manage your resources! The sky is the limit!


package IdleR_API;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;


public class WidgetFactoryDemo extends Application{

    public static void main(String[] args) {
        launch(args);
    }
    
    @Override
    public void start(Stage primaryStage) {
        //First, set up the game's mechanics
        //RESOURCES
        Resource dollar = new Resource("dollar", false, false);
        dollar.setQuant(100);  //you start with $100
        Resource wood = new Resource("wood", false, false);
        Resource iron = new Resource("iron", false, false);
        Resource widget = new Resource("widget", false, false);
        
        //DELTAS
        Delta[] mineCost = {new Delta(dollar, 10)};
        Delta[] mineProd = {new Delta(iron, 1)};
        Delta[] forestCost = {new Delta(dollar, 10)};
        Delta[] forestProd = {new Delta(wood, 1)};
        Delta[] factoryCost = {new Delta(dollar, 25)};
        Delta[] factoryCon = {(new Delta(wood, 3)), (new Delta(iron, 1))};
        Delta[] factoryProd = {new Delta(widget, 2)};
        Delta[] storeCost = {new Delta(dollar, 10)};
        Delta[] storeCon = {new Delta(widget, 1)};
        Delta[] storeProd = {new Delta(dollar, 5)};
        
        //FACTORS
        Factor mine = new Factor("Mine", mineCost, true, false);
        mine.setProduce(mineProd);
        mine.setDesc("+1 iron/s");
        Factor forest = new Factor("Forest", forestCost, true, false);
        forest.setProduce(forestProd);
        forest.setDesc("+1 wood/s");
        Factor factory = new Factor("Factory", factoryCost, true, false);
        factory.setConsume(factoryCon);
        factory.setProduce(factoryProd);
        factory.setDesc("3 wood + 1 iron = 2 widget/s");
        Factor store = new Factor("Store", storeCost, true, false);
        store.setConsume(storeCon);
        store.setProduce(storeProd);
        store.setDesc("1 widget = 5 dollar/s");
        
        Resource[] resources = {dollar, wood, iron, widget};
        Factor[] factors = {mine, forest, factory, store};
        
        //Now, do JavaFX stuff...
        // Create a pane and set its properties
        GridPane pane = new GridPane();
        pane.setPadding(new Insets(10, 10, 10, 10));
        pane.setHgap(10);
        pane.setVgap(5);
        
        resourceCard[] rc = new resourceCard[resources.length];
        for (int i = 0; i < resources.length; i++) {
            rc[i] = new resourceCard(resources[i]);
        }
        
        //this is the most concise line of code I've ever written
        //this command takes the gridpane & the factors as arguments
        //      and automatically formats them in the final scene
        FactorBuilderMaster(pane, factors, rc);
        ResourceMenu(pane, "Widget Factory", rc);
        
        spinTimer spin = new spinTimer();
        Text t = new Text();
        pane.add(t, 22, 0);
        GridPane.setHalignment(t, HPos.RIGHT);
        
        Timeline timeline = new Timeline(new KeyFrame(
                Duration.millis(1000),
                e -> {
                    spin.update();
                    t.setText(Character.toString(spin.tmp));
                    for (Factor factor : factors) {
                        factor.convert();
                    }
                    RefreshResources(rc);
                }
                
                ));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();

        // Create a scene and place it in the stage
        Scene scene = new Scene(pane);
        primaryStage.setWidth(1200);
        primaryStage.setHeight(600);
        primaryStage.setTitle("Widget Factory");
        primaryStage.setScene(scene); // Place the scene in the stage
        primaryStage.show(); // Display the stage (Show without this)
    }
    
    public void ResourceMenu (GridPane pane, String t, resourceCard[] rc) {
        Text title = new Text(t);
        title.setFont(Font.font("Arial", FontWeight.BOLD, 24)); 
        pane.add(title, 20, 0);
        
        for (int i = 0; i < rc.length; i++) {
            pane.add(rc[i].name, 21, i+2);
            pane.add(rc[i].quant, 22, i+2);
            GridPane.setHalignment(rc[i].quant, HPos.RIGHT);
        }
    }
    
    
    public void FactorBuilderMaster (GridPane pane, Factor[] factors, resourceCard[] rc) {
        // this method has an upper limit of 20 factorCards
        //      because that is what can fit on the 1200x600 pane
        factorCard[] factorCards = toFCArray(factors, rc);
        for (int i = 0; i < factorCards.length && i < 20; i++) {
            FactorBuilder(pane, factorCards[i], i);
        }
    }
    
    public void FactorBuilder (GridPane pane, factorCard fc, int n) {
        int j = n%5;
        int i = (n-j)/5;
        Text name = fc.name;
        name.setFont(Font.font("Arial", FontWeight.BOLD, 24)); 
        Text desc = fc.desc;
        Text cost = fc.cost;
        Text quant = fc.quant;
        quant.setFont(Font.font("Arial", FontWeight.BOLD, 24)); 

        Button buy = fc.buy;
        Button sell = fc.sell;
        
        pane.add(name, (i*5), (j*5));
        pane.add(desc, (i*5), (j*5)+1);
        pane.add(cost, (i*5), (j*5)+2);
        pane.add(buy, (i*5), (j*5)+3);
        pane.add(quant, (i*5)+3, (j*5));
        GridPane.setHalignment(quant, HPos.RIGHT);
        pane.add(sell, (i*5)+3, (j*5)+3);
        GridPane.setHalignment(sell, HPos.RIGHT);
    }
    
    public factorCard[] toFCArray (Factor[] array, resourceCard[] rc) {
        factorCard[] FCA = new factorCard[array.length];
        for (int i = 0; i < array.length; i++) {
            FCA[i] = new factorCard(array[i], rc);
        }
        return FCA;
    }
    
    class factorCard {
        private Text name;
        private Text desc;
        private Text cost;
        private Text quant;
        private Button buy = new Button("Buy");
        private Button sell = new Button("Sell");
        
        //CONSTRUCTOR
        public factorCard(Factor factor, resourceCard[] rc) {
            name = new Text(factor.getName());
            desc = new Text(factor.getDesc());
            cost = new Text(Integer.toString(factor.getCostInt())
                    + " " + factor.getCostString() + "s");
            quant = new Text(Integer.toString(factor.getQuant()));
            buy.setOnMouseClicked(e -> {       
                factor.buy(1);
                quant.setText(factor.getQuant() + "");
                RefreshResources(rc);
                
            });
            sell.setOnMouseClicked(e -> {       
                factor.sell(1);
                quant.setText(factor.getQuant() + "");
                RefreshResources(rc);
            });
            
        }
    }
    
    class resourceCard {
        private Text name;
        private Text quant;
        private Resource rec;
        
        //CONSTRUCTOR
        public resourceCard(Resource resource) {
            name = new Text(resource.getName());
            quant = new Text(resource.getQuant() + "");
            rec = resource;
        }
        
        public void refresh() {
            quant.setText(rec.getQuant() + "");
        }
    }
    
    public void RefreshResources (resourceCard[] rc) {
        for (resourceCard card : rc) {
            card.refresh();
        }
    }
    
    public class spinTimer {
        private int counter = 0;
        private char[] spinner = {'-','\\','|','/'};
        private char tmp;

        public void update() {
            tmp = spinner[counter];
            counter++;
            if (counter > 3) {
                counter = 0;
            }
        }
    }
}
