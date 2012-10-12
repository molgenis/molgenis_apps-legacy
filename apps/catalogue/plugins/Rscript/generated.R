
#this is self-generated script!

for(rowIndex in 1:nrow(dataSet)){
	observationTarget = rownames(dataSet)[rowIndex];



fasting_glucose_Prevend = 0;

if(!is.null(NULL) && !is.null(as.numeric(dataSet[rowIndex,"LV_GL_3"]))){
      
      if(fasting == "yes" || fasting == 1){
              
                fasting_glucose_Prevend = as.numeric(dataSet[rowIndex,"LV_GL_3"]);
      }  
}
fasting_glucose_Prevend = as.numeric(dataSet[rowIndex, "FAST_3"]);


parental_diabetes_mellitus_Prevend = 0;
if(as.numeric(dataSet[rowIndex, "V57A_1"]) == 1 || as.numeric(dataSet[rowIndex, "V57A_1"]) == "yes"){
	parental_diabetes_mellitus_Prevend = 1;
}
if(as.numeric(dataSet[rowIndex, "V57B_1"]) == 1 || as.numeric(dataSet[rowIndex, "V57B_1"]) == "yes"){
	parental_diabetes_mellitus_Prevend = 1;
}



current_smoker_Prevend = 0;
if(as.numeric(dataSet[rowIndex, "SMOKE_3"]) == 0){
	current_smoker_Prevend = 1;
}


age_Prevend = 0;
age_Prevend = as.numeric(dataSet[rowIndex, "AGE_3"]);


gender_Prevend = 0;
gender_Prevend = as.numeric(dataSet[rowIndex, "SEX"]);


body_mass_index_Prevend = 0;

if(!is.null(as.numeric(dataSet[rowIndex,"WEIGH_3"])) && !is.null(as.numeric(dataSet[rowIndex,"LENGT_3"]))){
    body_mass_index_Prevend = as.numeric(dataSet[rowIndex,"WEIGH_3"]) / (as.numeric(dataSet[rowIndex,"LENGT_3"]) * as.numeric(dataSet[rowIndex,"LENGT_3"]));
}
body_mass_index_Prevend = as.numeric(dataSet[rowIndex, "BMI_3"]);


hypertension_Prevend = 0;

if(!is.null(as.numeric(dataSet[rowIndex,"SBP_3"])) && !is.null(as.numeric(dataSet[rowIndex,"DBP_3"]))){
    if((as.numeric(dataSet[rowIndex,"SBP_3"]) / as.numeric(dataSet[rowIndex,"DBP_3"])) > (140/90)){
        hypertension_Prevend = 1;
    }
}
if(as.numeric(dataSet[rowIndex, "HTTR_0"]) == 1 || as.numeric(dataSet[rowIndex, "HTTR_0"]) == "yes"){
	hypertension_Prevend = 1;
}
if(as.numeric(dataSet[rowIndex, "V55C_1"]) == 1 || as.numeric(dataSet[rowIndex, "V55C_1"]) == "yes"){
	hypertension_Prevend = 1;
}



former_smoker_Prevend = 0;
if(as.numeric(dataSet[rowIndex, "SMOKE_3"]) == 0){
	former_smoker_Prevend = 1;
}


observedValue <- c(parental_diabetes_mellitus_Prevend,fasting_glucose_Prevend,current_smoker_Prevend,age_Prevend,body_mass_index_Prevend,gender_Prevend,hypertension_Prevend,former_smoker_Prevend);
featureName <- c("parental_diabetes_mellitus_Prevend","fasting_glucose_Prevend","current_smoker_Prevend","age_Prevend","body_mass_index_Prevend","gender_Prevend","hypertension_Prevend","former_smoker_Prevend");
add.observedvalue(investigation_name = "Prevend", target_name=observationTarget, feature_name = featureName, value = observedValue);


}