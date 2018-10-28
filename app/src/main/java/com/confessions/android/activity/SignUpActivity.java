package com.confessions.android.activity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;


import com.confessions.android.Age;
import com.confessions.android.R;
import com.confessions.android.Utils;
import com.confessions.android.Validator;
import com.confessions.android.retrofit.ApiClient;
import com.confessions.android.retrofit.SignUpRequest;
import com.confessions.android.retrofit.SignUpResponse;
import com.confessions.android.retrofit.UserCheckRequest;
import com.confessions.android.retrofit.UserCheckResponse;

import org.json.JSONArray;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SignUpActivity extends AppCompatActivity {
    private TextInputLayout txtInpUsername;
    private TextInputEditText txtUsername;
    private ProgressBar progressUsername;
    private TextView txtUsernameAvailable;
    private TextInputLayout txtInpPassword;
    private TextInputEditText txtPassword;
    private ImageView imgShowHidePassword;
    private TextInputLayout txtInpDob;
    private TextInputEditText txtDob;
    private Spinner countrySpinner;
    private RadioGroup rdGrpGender;
    private Button btnSignUp;


    private boolean usernameAvailable;
    private boolean passwordValid;
    private boolean dobValid;
    private boolean countryValid;
    private boolean genderValid;
    private String selectedCountry;
    private boolean passwordShown;
    private String selectedGender;
    private ImageView imgBack;
    private TextInputLayout txtInpEmail;
    private TextInputEditText txtEmail;
    private TextInputLayout txtInpSecretAnswer;
    private TextInputEditText txtSecretAnswer;
    private Spinner spnSecretQuestion;
    private String secretQuestion;
    private String secretAnswer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        initializeViews();
        attachListeners();
    }

    private void initializeViews() {
        txtInpUsername=findViewById(R.id.txtInpUsername);
        txtUsername=findViewById(R.id.txtUsername);
        progressUsername=findViewById(R.id.progressUsername);
        txtUsernameAvailable=findViewById(R.id.txtUsernameAvailable);
        txtInpPassword=findViewById(R.id.txtInpPassword);
        txtPassword=findViewById(R.id.txtPassword);
        imgShowHidePassword=findViewById(R.id.imgShowHidePassword);
        txtInpDob=findViewById(R.id.txtInpDob);
        txtDob=findViewById(R.id.txtDob);
        countrySpinner=findViewById(R.id.spnCountry);
        rdGrpGender=findViewById(R.id.rdGrpGender);
        btnSignUp=findViewById(R.id.btnSignUp);
        imgBack=findViewById(R.id.imgBack);
        txtInpEmail=findViewById(R.id.txtInpEmail);
        txtEmail=findViewById(R.id.txtEmail);
        txtInpSecretAnswer=findViewById(R.id.txtInpSecretAnswer);
        txtSecretAnswer=findViewById(R.id.txtSecretAnswer);
        spnSecretQuestion=findViewById(R.id.spnSecretQuestion);
    }

    private void attachListeners() {
        txtUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(Validator.validateUsername(s)){
                    progressUsername.setVisibility(View.VISIBLE);
                    ApiClient.getClient().checkUsernameAvailability(new UserCheckRequest(s.toString())).enqueue(new Callback<UserCheckResponse>() {
                        @Override
                        public void onResponse(Call<UserCheckResponse> call, Response<UserCheckResponse> response) {
                            progressUsername.setVisibility(View.GONE);
                            if(response.body().isSuccess()){
                                usernameAvailable=true;
                                txtUsernameAvailable.setVisibility(View.VISIBLE);
                                txtUsernameAvailable.setText(response.body().getData());
                                txtInpUsername.setError(null);
                                txtInpUsername.setErrorEnabled(false);
                            }else{
                                usernameAvailable=false;
                                txtUsernameAvailable.setVisibility(View.GONE);
                                txtInpUsername.setErrorEnabled(true);
                                txtInpUsername.setError(response.body().getData());
                            }
                        }

                        @Override
                        public void onFailure(Call<UserCheckResponse> call, Throwable t) {
                            usernameAvailable=false;
                            txtUsernameAvailable.setVisibility(View.GONE);
                            txtInpUsername.setErrorEnabled(true);
                            txtInpUsername.setError("Something went wrong. Please retype the username");
                            progressUsername.setVisibility(View.GONE);
                        }
                    });
                }else{
                    progressUsername.setVisibility(View.GONE);
                    txtInpUsername.setErrorEnabled(true);
                    txtInpUsername.setError("not a valid username");
                    txtUsernameAvailable.setVisibility(View.GONE);
                    usernameAvailable=false;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        txtPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length()<6){
                    txtInpPassword.setErrorEnabled(true);
                    txtInpPassword.setError("minimum 6 alphanumeric characters required");
                    passwordValid=false;
                }else{
                    txtInpPassword.setError(null);
                    txtInpPassword.setErrorEnabled(false);
                    passwordValid=true;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        imgShowHidePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(passwordShown){
                    txtPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    passwordShown=false;
                    imgShowHidePassword.setImageResource(R.drawable.ic_hide);
                }else{
                    txtPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    passwordShown=true;
                    imgShowHidePassword.setImageResource(R.drawable.ic_show);
                }
            }
        });

        txtDob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar myCalendar=Calendar.getInstance();
                String myFormat = "dd/MM/yyyy"; //In which you need put here
                final SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());

                DatePickerDialog datePickerDialog= new DatePickerDialog(SignUpActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        myCalendar.set(year,month,dayOfMonth);
                        txtDob.setText(sdf.format(myCalendar.getTime()));
                        Date selectedDate=new Date(myCalendar.getTime().getTime());
                        Age age= Utils.calculateAge(selectedDate);
                        Log.d("awesome","Age is: "+age.toString());
                        if(age.getYears()<16){
                            txtInpDob.setErrorEnabled(true);
                            txtInpDob.setError("you must be at least 16");
                            dobValid=false;
                        }else{
                            txtInpDob.setError(null);
                            txtInpDob.setErrorEnabled(false);
                            dobValid=true;
                        }
                    }
                },myCalendar.get(Calendar.YEAR),myCalendar.get(Calendar.MONTH),myCalendar.get(Calendar.DAY_OF_MONTH));

                datePickerDialog.getDatePicker().setMaxDate(myCalendar.getTime().getTime());
                datePickerDialog.show();
            }
        });

        try{
            String countriesArrayString=Utils.getJsonFromAsset(SignUpActivity.this,"countries.json");
            JSONArray countriesJsonArray=new JSONArray(countriesArrayString);
            ArrayList<String> countriesArrayList=new ArrayList<>();
            for(int i=0;i<countriesJsonArray.length();i++){
                countriesArrayList.add(countriesJsonArray.getJSONObject(i).getString("name"));
            }

            countriesArrayList.add(0,"Select Country");
            countrySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    selectedCountry=parent.getItemAtPosition(position).toString();
                    Log.d("awesome","selected country:"+selectedCountry);
                    if(selectedCountry.equals("Select Country")){
                        countryValid=false;
                    }else{
                        countryValid=true;
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    Log.d("awesome","nothing selected");
                }
            });
            ArrayAdapter<String> countryAdapter=new ArrayAdapter<>(SignUpActivity.this,android.R.layout.simple_dropdown_item_1line,countriesArrayList);
            countryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            countrySpinner.setAdapter(countryAdapter);
        }catch (Exception e){
            Log.d("awesome","Error in creating country adapter: "+e.toString());
        }

        //Spinner for security questions
        String[] secretQuestions={
          "Choose secret question",
          "What was your childhood nickname?",
          "What is the name of your favorite childhood friend?",
          "In what city or town did your mother and father meet?",
          "What is your favorite team?",
          "What is your favorite movie?",
          "What was your favorite sport in high school?",
          "What was your favorite food as a child?",
          "Who is your childhood sports hero?",
          "What school did you attend for sixth grade?",
          "In what town was your first job?"
        };



        ArrayAdapter<String> secretQuestionsAdapter=new ArrayAdapter<>(SignUpActivity.this,android.R.layout.simple_dropdown_item_1line,secretQuestions);
        secretQuestionsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnSecretQuestion.setAdapter(secretQuestionsAdapter);

        spnSecretQuestion.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position!=0){
                    secretQuestion=parent.getItemAtPosition(position).toString();
                }else{
                    secretQuestion=null;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                secretQuestion=null;
            }
        });

        txtSecretAnswer.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length()<3){
                    txtInpSecretAnswer.setErrorEnabled(true);
                    txtInpSecretAnswer.setError("atleast 3 characters required");
                }else{
                    txtInpSecretAnswer.setError(null);
                    txtInpSecretAnswer.setErrorEnabled(false);
                    secretAnswer=s.toString();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        rdGrpGender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                Log.d("awesome","checked: "+checkedId);
                genderValid=true;
                if(checkedId==R.id.rdMale){
                    selectedGender="male";
                }else{
                    selectedGender="female";
                }
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("awesome","results: "+usernameAvailable+","+passwordValid+","+dobValid+","+countryValid+","+genderValid);
                if(usernameAvailable
                        && passwordValid
                        && dobValid
                        && genderValid
                        && countryValid
                        && secretQuestion!=null
                        && secretQuestion!=""
                        && secretAnswer!=""
                        && secretAnswer!=null){
                    signup(txtUsername.getText().toString(),txtPassword.getText().toString(),txtDob.getText().toString(),selectedCountry,selectedGender);
                }else{
                    Snackbar.make(findViewById(R.id.cardView),"Please complete all the details first",Snackbar.LENGTH_LONG).show();
                }
            }
        });

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        txtEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(Validator.validateEmail(s)){
                    txtInpEmail.setError(null);
                    txtInpEmail.setErrorEnabled(false);
                }else{
                    txtInpEmail.setErrorEnabled(true);
                    txtInpEmail.setError("invalid email address");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    private void signup(String username, String password, String dob, String country, String gender) {
        Log.d("awesome","Signing up with: {"+username+","+password+","+dob+","+country+","+gender);
        final ProgressDialog progressDialog=new ProgressDialog(SignUpActivity.this);
        progressDialog.setTitle("Connecting you to Confessions");
        progressDialog.setMessage("Please wait...");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.show();
        btnSignUp.setEnabled(false);

        ApiClient.getClient().signup(new SignUpRequest(username,password,dob,country,gender,txtEmail.getText().toString(),secretQuestion,secretAnswer)).enqueue(new Callback<SignUpResponse>() {
            @Override
            public void onResponse(Call<SignUpResponse> call, Response<SignUpResponse> response) {
                progressDialog.dismiss();
                if(response.body().isSuccess()){
                    Snackbar.make(findViewById(R.id.cardView),response.body().getData(),Snackbar.LENGTH_LONG).show();
                    Handler handler=new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            finish();
                        }
                    },1000);
                }else{
                    btnSignUp.setEnabled(true);
                    Snackbar.make(findViewById(R.id.cardView),response.body().getData(),Snackbar.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<SignUpResponse> call, Throwable t) {
                btnSignUp.setEnabled(true);
                Snackbar.make(findViewById(R.id.cardView),"Something went wrong. Please try again",Snackbar.LENGTH_LONG).show();
            }
        });
    }
}
