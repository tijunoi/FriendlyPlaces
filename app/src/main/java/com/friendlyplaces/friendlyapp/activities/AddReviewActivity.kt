package com.friendlyplaces.friendlyapp.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import butterknife.BindView
import com.friendlyplaces.friendlyapp.R
import com.stepstone.stepper.StepperLayout
import com.stepstone.stepper.VerificationError

class AddReviewActivity : AppCompatActivity(), StepperLayout.StepperListener {

    @BindView(R.id.stepper_layout)
    lateinit var mStepperLayout: StepperLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_review)
    }

    override fun onStepSelected(newStepPosition: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onError(verificationError: VerificationError?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onReturn() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onCompleted(completeButton: View?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
