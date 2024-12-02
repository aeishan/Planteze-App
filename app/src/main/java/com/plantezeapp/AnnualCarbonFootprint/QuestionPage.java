package com.plantezeapp.AnnualCarbonFootprint;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.plantezeapp.Database.CarbonFootprint;
import com.plantezeapp.Database.FirebaseHelper;
import com.plantezeapp.Database.User;
import com.plantezeapp.R;
import com.plantezeapp.UserLogin.RegisterAccountPage;
import com.plantezeapp.UserLogin.WelcomePage;

import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.InputStream;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;


public class QuestionPage extends AppCompatActivity implements View.OnClickListener, FirebaseHelper.UserFetchListener{

    TextView question_here; // make sure to isolate the "other" option when they choose what house they have
    Button Next;
    ConstraintLayout constraintLayout;
    private List<View> dynamicButtons = new ArrayList<>();
    String selectedAnswer = "";
    private String clothesFrequency;
    static double transportationEmission;
    static double foodEmission;
    static double housingEmission;
    static double consumptionEmission;
    private HashMap<String, Integer> transportationCO2;
    private HashMap<String, Double> carC02;

    private HashMap<String, Integer> sFlights;
    private HashMap<String, Integer> lFlights;
    private HashMap<String, Integer> meat_b;
    private HashMap<String, Integer> meat_p;
    private HashMap<String, Integer> meat_c;
    private HashMap<String, Integer> meat_f;
    private HashMap<String, Integer> clothing;
    private HashMap<String, Integer> eDevices;
    private HashMap<String, Double> consumptionReductionFactors;
    private int housingValue;

    private int currentQuestionIndex = 0;
    private int count = 0;
    private String numDevices;
    private String home;
    private String occupants;
    private String area;
    private String bill;
    private String heat_home;
    private HashMap<String, String> answers;
    private User user;
    private FirebaseUser userFire;
    private FirebaseHelper help;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_question_page);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.constraintLayout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        question_here = findViewById(R.id.question);
        constraintLayout = findViewById(R.id.constraintLayout);
        Next = findViewById(R.id.nextButton);
        transportationEmission = 0;
        foodEmission = 0;
        housingEmission = 0;
        consumptionEmission = 0;
        clothesFrequency = "";
        numDevices = "";
        home = "";
        occupants = "";
        area = "";
        bill = "";
        heat_home = "";

        transportationCO2 = new HashMap<>();
        carC02 = new HashMap<>();
        sFlights = new HashMap<>();
        lFlights = new HashMap<>();
        meat_b = new HashMap<>();
        meat_p = new HashMap<>();
        meat_c = new HashMap<>();
        meat_f = new HashMap<>();
        clothing = new HashMap<>();
        eDevices = new HashMap<>();
        consumptionReductionFactors = new HashMap<>();
        answers = new HashMap<>();

        sFlights.put("None", 0);
        sFlights.put("1-2 flights", 225);
        sFlights.put("3-5 flights", 600);
        sFlights.put("6-10 flights", 1200);
        sFlights.put("More than 10 flights", 1800);

        lFlights.put("None", 0);
        lFlights.put("1-2 flights", 825);
        lFlights.put("3-5 flights", 2200);
        lFlights.put("6-10 flights", 4400);
        lFlights.put("More than 10 flights", 6600);

        clothing.put("Monthly", 360);
        clothing.put("Quarterly", 120);
        clothing.put("Annually", 100);
        clothing.put("Rarely", 5);

        eDevices.put("None", 0);
        eDevices.put("1", 300);
        eDevices.put("2", 600);
        eDevices.put("3 or more", 900);

        consumptionReductionFactors.put("Occasionally", 0.15);
        consumptionReductionFactors.put("Frequently", 0.3);
        consumptionReductionFactors.put("Always", 0.15);



        Next.setOnClickListener(this);

        loadQuestion();

    }

    @Override
    public void onClick(View v) {

        for (View button: dynamicButtons){
            button.setBackgroundColor(Color.parseColor("#d8dbe2"));
        }

        Button clickedButton = (Button) v;
        if (clickedButton.getId() == Next.getId()){
            answers.put("Q" + currentQuestionIndex, selectedAnswer);
            if (currentQuestionIndex < QuestionAnswer.question.length - 1) {


                if (selectedAnswer == "") {
                    Toast.makeText(QuestionPage.this, "Please select an option", Toast.LENGTH_SHORT).show();
                } else {
                    if (currentQuestionIndex == 5){
                        transportationEmission = transportationEmission + sFlights.get(selectedAnswer);
                        Toast.makeText(QuestionPage.this, "hi " + transportationEmission, Toast.LENGTH_SHORT).show();

                    }
                    else if (currentQuestionIndex == 6){
                        transportationEmission = transportationEmission + lFlights.get(selectedAnswer);
                        Toast.makeText(QuestionPage.this, "hi " + transportationEmission, Toast.LENGTH_SHORT).show();
                    }
                    else if (currentQuestionIndex == 3 && selectedAnswer != "Never"){
                        createPublicTransportEmission(selectedAnswer);
                    }
                    else if (currentQuestionIndex == 4 && count != 0){
                        transportationEmission = transportationEmission + transportationCO2.get(selectedAnswer);
                        Toast.makeText(QuestionPage.this, "hi " + transportationEmission, Toast.LENGTH_SHORT).show();
                    }
                    else if (currentQuestionIndex == 1){
                        createCarEmission(selectedAnswer);
                    }
                    else if (currentQuestionIndex == 2){
                        transportationEmission = transportationEmission + carC02.get(selectedAnswer);
                        Toast.makeText(QuestionPage.this, "hi " + transportationEmission, Toast.LENGTH_SHORT).show();
                    }
                    else if (currentQuestionIndex == 7 && !selectedAnswer.equals("Meat-based (eat all types of animal products)")){
                        diet(selectedAnswer);
                        Toast.makeText(QuestionPage.this, "hi " + foodEmission, Toast.LENGTH_SHORT).show();
                    }
                    else if (currentQuestionIndex == 7){
                        createMeatEmissions();
                    }
                    else if (currentQuestionIndex == 8){
                        foodEmission = foodEmission + meat_b.get(selectedAnswer);
                        Toast.makeText(QuestionPage.this, "hi " + foodEmission, Toast.LENGTH_SHORT).show();
                    }
                    else if (currentQuestionIndex == 9){
                        foodEmission = foodEmission + meat_p.get(selectedAnswer);
                        Toast.makeText(QuestionPage.this, "hi " + foodEmission, Toast.LENGTH_SHORT).show();
                    }
                    else if (currentQuestionIndex == 10){
                        foodEmission = foodEmission + meat_c.get(selectedAnswer);
                        Toast.makeText(QuestionPage.this, "hi " + foodEmission, Toast.LENGTH_SHORT).show();
                    }
                    else if (currentQuestionIndex == 11){
                        foodEmission = foodEmission + meat_f.get(selectedAnswer);
                        Toast.makeText(QuestionPage.this, "hi " + foodEmission, Toast.LENGTH_SHORT).show();
                    }
                    else if (currentQuestionIndex == 12){
                        wasteFood(selectedAnswer);
                        Toast.makeText(QuestionPage.this, "hi " + foodEmission, Toast.LENGTH_SHORT).show();
                    }
                    else if (currentQuestionIndex == 13){
                        if (selectedAnswer.equals("Other")){
                            home = "Townhouse";
                        }
                        else{
                            home = selectedAnswer;
                        }
                    }
                    else if (currentQuestionIndex == 14){
                        occupants = selectedAnswer;
                    }
                    else if (currentQuestionIndex == 15){
                        area = selectedAnswer;
                    }
                    else if (currentQuestionIndex == 16){
                       heat_home = selectedAnswer;
                    }
                    else if (currentQuestionIndex == 17){
                        bill = selectedAnswer;
                    }
                    else if (currentQuestionIndex == 18){

                        if (heat_home.equals("Other")){
                            double val = createJSON(home, occupants, area, bill, "Natural Gas") + createJSON(home, occupants, area, bill, "Electricity") + createJSON(home, occupants, area, bill, "Oil") + createJSON(home, occupants, area, bill, "Propane") + createJSON(home, occupants, area, bill, "Wood");
                            val = val / 5;

                            housingEmission = housingEmission + val;
                        }
                        else{
                            housingEmission = housingEmission + createJSON(home, occupants, area, bill, heat_home);
                        }
                        Toast.makeText(QuestionPage.this, "first: " + housingEmission, Toast.LENGTH_SHORT).show();

                        if (selectedAnswer.equals("Other")){
                            double val2 = createJSON(home, occupants, area, bill, "Natural Gas") + createJSON(home, occupants, area, bill, "Electricity") + createJSON(home, occupants, area, bill, "Oil") + createJSON(home, occupants, area, bill, "Propane") + createJSON(home, occupants, area, bill, "Solar");
                            val2 = val2 / 5;

                            housingEmission = housingEmission + val2;
                        }
                        else{
                            housingEmission = housingEmission + createJSON(home, occupants, area, bill, selectedAnswer);
                        }

                        Toast.makeText(QuestionPage.this, "second " + housingEmission, Toast.LENGTH_SHORT).show();
                    }
                    else if (currentQuestionIndex == 19){
                        if (selectedAnswer.equals("Yes, primarily (more than 50% of energy use)")){
                            housingEmission = housingEmission - 6000;
                        }
                        else if (selectedAnswer.equals("Yes, partially (less than 50% of energy use)")){
                            housingEmission = housingEmission - 4000;
                        }
                        Toast.makeText(QuestionPage.this, "hi " + housingEmission, Toast.LENGTH_SHORT).show();
                    }
                    else if (currentQuestionIndex == 20){
                        consumptionEmission = consumptionEmission + clothing.get(selectedAnswer);
                        clothesFrequency = selectedAnswer;
                        Toast.makeText(QuestionPage.this, "hi " + consumptionEmission, Toast.LENGTH_SHORT).show();
                    }
                    else if (currentQuestionIndex == 21){
                        ecoFriendlyProducts(selectedAnswer);
                        Toast.makeText(QuestionPage.this, "hi " + consumptionEmission, Toast.LENGTH_SHORT).show();
                    }
                    else if (currentQuestionIndex == 22){
                        consumptionEmission = consumptionEmission + eDevices.get(selectedAnswer);
                        numDevices = selectedAnswer;
                        Toast.makeText(QuestionPage.this, "hi " + consumptionEmission, Toast.LENGTH_SHORT).show();
                    }

                    if (selectedAnswer == "No" && currentQuestionIndex == 0){
                        currentQuestionIndex = 2;
                    }
                    if (selectedAnswer != "Meat-based (eat all types of animal products)" && currentQuestionIndex == 7){
                        currentQuestionIndex = 11;
                    }
                    currentQuestionIndex++;
                    loadQuestion();
                }
            }
            else{
                reduceConsumptionEmission(numDevices, selectedAnswer);
                reduceConsumptionEmission2(clothesFrequency, selectedAnswer);
                Toast.makeText(QuestionPage.this, "hi " + consumptionEmission, Toast.LENGTH_SHORT).show();
                Toast.makeText(QuestionPage.this, "You're DONE!!!", Toast.LENGTH_SHORT).show();

                checkForNegatives();

                answers.put("transportationE", "" + transportationEmission);
                answers.put("foodE", "" + foodEmission);
                answers.put("housingE", "" + housingEmission);
                answers.put("consumptionE", "" + consumptionEmission);
                answers.put("country", IntroPage.item);
                answers.put("countryValue", "" + IntroPage.countryValues.get(IntroPage.item));

                Log.d("MainActivity", "we here!!");
                userFire = FirebaseAuth.getInstance().getCurrentUser();
                Log.d("MainActivity", "we here2!!");
                help = new FirebaseHelper();
                Log.d("MainActivity", "we here3!!");
                help.fetchUser(userFire.getUid(), this);
                Log.d("MainActivity", "we here4!!");

                Intent intent=new Intent(QuestionPage.this, CarbonFootprintBreakdown2.class);
                startActivity(intent);
            }



        }
        else{
            selectedAnswer = clickedButton.getText().toString();
            clickedButton.setBackgroundColor(Color.parseColor("#009999"));
            Toast.makeText(QuestionPage.this, "You selected: " + clickedButton.getText(), Toast.LENGTH_SHORT).show();

        }

    }

    void loadQuestion(){
        clearButtons();
        selectedAnswer = "";

        question_here.setText(QuestionAnswer.question[currentQuestionIndex]);

        int previousViewId = R.id.question;

        if (currentQuestionIndex == QuestionAnswer.question.length - 1){
            Next.setText("Finish Survey");
        }

        for (String option: QuestionAnswer.answers[currentQuestionIndex]){
            Button button = new Button(this);
            button.setId(View.generateViewId());
            button.setText(option);

            constraintLayout.addView(button);
            dynamicButtons.add(button);

            ConstraintSet constraintSet = new ConstraintSet();
            constraintSet.clone(constraintLayout);

            constraintSet.connect(button.getId(), ConstraintSet.TOP, previousViewId, ConstraintSet.BOTTOM, 16);
            constraintSet.connect(button.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, 16);
            constraintSet.connect(button.getId(), ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, 16);

            constraintSet.constrainWidth(button.getId(), ConstraintSet.MATCH_CONSTRAINT);

            constraintSet.constrainHeight(button.getId(), ConstraintSet.WRAP_CONTENT);


            constraintSet.applyTo(constraintLayout);

            button.setOnClickListener(this);

            previousViewId = button.getId();

        }

        ConstraintSet nextConstraintSet = new ConstraintSet();
        nextConstraintSet.clone(constraintLayout);

        nextConstraintSet.connect(Next.getId(), ConstraintSet.TOP, previousViewId, ConstraintSet.BOTTOM, 16);
        nextConstraintSet.connect(Next.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, 16);
        nextConstraintSet.connect(Next.getId(), ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, 16);

        nextConstraintSet.applyTo(constraintLayout);

    }

    void createCarEmission(String type){
        double factor = 0;
        if (type == "Gasoline") {
            factor = 0.24;
        }
        else if (type == "Diesel") {
            factor = 0.27;
        }
        else if (type == "Hybrid") {
            factor = 0.16;
        }
        else if (type == "Electric") {
            factor = 0.05;
        }
        else{
            factor = 0.18; // average of all other choices
        }

        carC02.put("Up to 5,000 km (3,000 miles)", 5000 * factor);
        carC02.put("5,000–10,000 km (3,000–6,000 miles)", 10000 * factor);
        carC02.put("10,000–15,000 km (6,000–9,000 miles)", 15000 * factor);
        carC02.put("15,000–20,000 km (9,000–12,000 miles)", 20000 * factor);
        carC02.put("20,000–25,000 km (12,000–15,000 miles)", 25000 * factor);
        carC02.put("More than 25,000 km (15,000 miles)", 35000 * factor);
    }

    void createPublicTransportEmission(String frequency){
        if (frequency == "Occasionally (1-2 times/week)"){
            transportationCO2.put("Under 1 hour", 246);
            transportationCO2.put("1-3 hours", 819);
            transportationCO2.put("3-5 hours", 1638);
            transportationCO2.put("5-10 hours", 3071);
            transportationCO2.put("More than 10 hours", 4095);

        }
        else if (frequency == "Frequently (3-4 times/week)"){
            transportationCO2.put("Under 1 hour", 573);
            transportationCO2.put("1-3 hours", 1911);
            transportationCO2.put("3-5 hours", 3822);
            transportationCO2.put("5-10 hours", 7166);
            transportationCO2.put("More than 10 hours", 9555);

        }
        else if (frequency == "Always (5+ times/week)"){
            transportationCO2.put("Under 1 hour", 1050);
            transportationCO2.put("1-3 hours", 2363);
            transportationCO2.put("3-5 hours", 4103);
            transportationCO2.put("5-10 hours", 9611);
            transportationCO2.put("More than 10 hours", 13750);

        }
        count = 1;
    }

    void diet(String type){
        if (type.equals("Vegetarian")){
            foodEmission = foodEmission + 1000;
        }
        else if (type.equals("Vegan")){
            foodEmission = foodEmission + 500;
        }
        else if (type.equals("Pescatarian (fish/seafood)")){
            foodEmission = foodEmission + 1500;
        }
    }

    void createMeatEmissions(){
        meat_b.put("Daily", 2500);
        meat_b.put("Frequently (3-5 times/week)", 1900);
        meat_b.put("Occasionally (1-2 times/week)", 1300);
        meat_b.put("Never", 0);

        meat_p.put("Daily", 1450);
        meat_p.put("Frequently (3-5 times/week)", 860);
        meat_p.put("Occasionally (1-2 times/week)", 450);
        meat_p.put("Never", 0);

        meat_c.put("Daily", 950);
        meat_c.put("Frequently (3-5 times/week)", 600);
        meat_c.put("Occasionally (1-2 times/week)", 200);
        meat_c.put("Never", 0);

        meat_f.put("Daily", 800);
        meat_f.put("Frequently (3-5 times/week)", 500);
        meat_f.put("Occasionally (1-2 times/week)", 150);
        meat_f.put("Never", 0);
    }

    void wasteFood(String ans){
        if (ans.equals("Rarely")){
            foodEmission = foodEmission + 23.4;
        }
        else if (ans.equals("Occasionally")){
            foodEmission = foodEmission + 70.2;
        }
        else if (ans.equals("Frequently")){
            foodEmission = foodEmission + 140.4;
        }
    }

    void ecoFriendlyProducts(String ans){
        if (ans.equals("Yes, regularly")){
            consumptionEmission = consumptionEmission * 0.5;
        }
        else if (ans.equals("Yes, occasionally")){
            consumptionEmission = consumptionEmission * 0.7;

        }
    }

    void reduceConsumptionEmission(String number, String freq){
        if (number.equals("1")){
            if (freq.equals("Occasionally")){
                consumptionEmission = consumptionEmission - 45;
            }
            else if (freq.equals("Frequently")){
                consumptionEmission = consumptionEmission - 60;
            }
            else if (freq.equals("Always")){
                consumptionEmission = consumptionEmission - 90;
            }
        }
        else if (number.equals("2")){
            int dec = 60;
            if (freq.equals("Occasionally")){
                consumptionEmission = consumptionEmission - dec;
            }
            else if (freq.equals("Frequently")){
                consumptionEmission = consumptionEmission - (2 * dec);
            }
            else if (freq.equals("Always")){
                consumptionEmission = consumptionEmission - (3 * dec);
            }
        }
        else if (number.equals("3 or more")){  // add 3 or more??
            int dec = 90;
            if (freq.equals("Occasionally")){
                consumptionEmission = consumptionEmission - dec;
            }
            else if (freq.equals("Frequently")){
                consumptionEmission = consumptionEmission - (2 * dec);
            }
            else if (freq.equals("Always")){
                consumptionEmission = consumptionEmission - (3 * dec);
            }
        }
    }

    void reduceConsumptionEmission2(String clothes, String freq){
        Double factor = 0.0;

        if (!freq.equals("Never")) {
            factor = consumptionReductionFactors.get(freq);
        }

        consumptionEmission = consumptionEmission - (clothing.get(clothes) * factor);

    }

    int createJSON(String house, String people, String size, String money, String type){
        try{
            InputStream inputStream = getResources().openRawResource(R.raw.housing);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            StringBuilder jsonStringBuilder = new StringBuilder();

            String line;

            while ((line = reader.readLine()) != null) {
                jsonStringBuilder.append(line);
            }

            // Parse the JSON string
            JSONObject jsonObject = new JSONObject(jsonStringBuilder.toString());

            int value = jsonObject
                    .getJSONObject(house)
                    .getJSONObject(people)
                    .getJSONObject(size)
                    .getJSONObject(money)
                    .getInt(type);

            return value;
        }

        catch (Exception e){
            System.out.println("You got an error: " + e);
            return 0;
        }
    }

    void checkForNegatives(){
        if (housingEmission < 0){
            housingEmission = 0;
        }
        if (consumptionEmission < 0){
            consumptionEmission = 0;
        }
    }

    void clearButtons(){
        for (View button: dynamicButtons){
            constraintLayout.removeView(button);
        }
        dynamicButtons.clear();
    }

    @Override
    public void onUserFetched(User user) {
        Log.d("MainActivity", "User fetched: " + user.getEmail());
        this.user = user;

        CarbonFootprint cfoot = user.getCarbonFootprint();
        Log.d("MainActivity", "is NULL?? : " + cfoot);


        cfoot.setAnswers(answers);

        user.setuID(userFire.getUid());
        help.saveUser(user);
    }

    @Override
    public void onFetchFailed(String errorMessage) {
        Log.d("MainActivity", "Error: User not Fetched" );

        user = new User(userFire.getUid(), userFire.getEmail());
        CarbonFootprint cfoot = user.getCarbonFootprint();
        cfoot.setAnswers(answers);
        help.saveUser(user);
    }
}
