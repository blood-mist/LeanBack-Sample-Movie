/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package stb.androidtv.moviesleanback.leanDialog;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v17.leanback.app.ErrorFragment;
import android.support.v17.leanback.app.ErrorSupportFragment;
import android.support.v17.leanback.app.GuidedStepFragment;
import android.support.v17.leanback.app.GuidedStepSupportFragment;
import android.support.v17.leanback.widget.GuidanceStylist.Guidance;
import android.support.v17.leanback.widget.GuidedAction;
import android.view.View;
import android.widget.Toast;

import java.util.List;

import stb.androidtv.moviesleanback.R;
import stb.androidtv.moviesleanback.login.LoginActivity;

import static stb.androidtv.moviesleanback.utils.LinkConfig.ERROR_MESSAGE;
import static stb.androidtv.moviesleanback.utils.LinkConfig.ERROR_TITLE;

/**
 * TODO: Javadoc
 */
public class DialogExampleFragment extends ErrorSupportFragment {

    private static final int ACTION_ID_POSITIVE = 1;
    private static final int ACTION_ID_NEGATIVE = ACTION_ID_POSITIVE + 1;
    private String errorTitle,errorMessage;
    private static final boolean TRANSLUCENT = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getActivity()!=null) {
            errorTitle = getArguments().getString(ERROR_TITLE);
            errorMessage = getArguments().getString(ERROR_MESSAGE);
        }
        setTitle(errorTitle);
        setErrorContent();
    }

    private void setErrorContent() {
        setImageDrawable(getActivity().getDrawable(R.drawable.lb_ic_sad_cloud));
        setMessage(errorMessage);
        setDefaultBackground(TRANSLUCENT);
        setButtonText(getResources().getString(R.string.dismiss_error));
        setButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                getFragmentManager().beginTransaction().remove(DialogExampleFragment.this).commit();
                Intent intent=new Intent(getActivity().getBaseContext(), LoginActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });
    }



}