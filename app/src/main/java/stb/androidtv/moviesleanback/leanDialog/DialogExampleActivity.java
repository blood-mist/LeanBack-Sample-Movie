/*
 * Copyright (C) 2014 The Android Open Source Project
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

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v17.leanback.app.ErrorSupportFragment;
import android.support.v17.leanback.app.GuidedStepFragment;
import android.support.v17.leanback.app.GuidedStepSupportFragment;
import android.support.v4.app.FragmentActivity;

import stb.androidtv.moviesleanback.R;

import static stb.androidtv.moviesleanback.utils.LinkConfig.ERROR_MESSAGE;
import static stb.androidtv.moviesleanback.utils.LinkConfig.ERROR_TITLE;

/**
 * TODO: Javadoc
 */
public class DialogExampleActivity extends FragmentActivity {

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#21272A")));

        if (savedInstanceState == null) {
            ErrorSupportFragment fragment = new DialogExampleFragment();
            Bundle bundle =new Bundle();
            bundle.putString(ERROR_TITLE,getIntent().getStringExtra(ERROR_TITLE));
            bundle.putString(ERROR_MESSAGE,getIntent().getStringExtra(ERROR_MESSAGE));
            fragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().add(R.id.main_browse_fragment,fragment).commit();
        }
    }
}