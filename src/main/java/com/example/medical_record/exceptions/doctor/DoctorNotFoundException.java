package com.example.medical_record.exceptions.doctor;

public class DoctorNotFoundException extends Exception
{
    public DoctorNotFoundException(String message)
    {
        super(message);
    }
}
