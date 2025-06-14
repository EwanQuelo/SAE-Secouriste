package fr.erm.sae201.vue.user;
import javafx.scene.control.Label;

public class SecouristeDashboard {
    
    Label welcomeLabel;

    public SecouristeDashboard() {
        //hello world 
        welcomeLabel = new Label("Bienvenue sur le tableau de bord du secouriste !");
        
    }

    public Label getView() {
        return welcomeLabel;
    }

    
}
