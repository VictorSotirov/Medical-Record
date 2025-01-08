package com.example.medical_record.exceptions.patient;

public class PatientNotFoundException extends Exception
{
    public PatientNotFoundException(String message)
    {
        super(message);
    }
}
