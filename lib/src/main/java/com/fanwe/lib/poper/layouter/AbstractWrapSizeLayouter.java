/*
 * Copyright (C) 2017 zhengjun, fanwe (http://www.fanwe.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.fanwe.lib.poper.layouter;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

abstract class AbstractWrapSizeLayouter implements PopLayouter
{
    private final boolean mIsDebug;

    public AbstractWrapSizeLayouter()
    {
        this(false);
    }

    public AbstractWrapSizeLayouter(boolean isDebug)
    {
        mIsDebug = isDebug;
    }

    @Override
    public final void layout(View popView, View popViewParent, View targetView)
    {
        final int parentSize = getParentSize(popViewParent);
        if (parentSize <= 0)
        {
            return;
        }

        int consume = 0;

        final int start = getStartBound(popView);
        if (start < 0)
        {
            consume += (-start);
        }

        final int end = getEndBound(popView);
        if (end > parentSize)
        {
            consume += (end - parentSize);
        }

        final ViewGroup.LayoutParams params = popView.getLayoutParams();
        final int layoutParamsSize = getLayoutParamsSize(params);
        if (consume > 0)
        {
            final int size = getPopSize(popView);
            int newSize = size - consume;
            if (newSize < 0)
            {
                newSize = 0;
            }

            if (layoutParamsSize == newSize && newSize == 0)
            {
                if (mIsDebug)
                {
                    Log.e(getDebugTag(), "ignored layoutParamsSize == newSize && newSize == 0");
                }
            } else
            {
                // 直接赋值，不检查layoutParamsSize != newSize，因为有时候setLayoutParams(params)执行一次无效
                setLayoutParamsSize(params, newSize);
                popView.setLayoutParams(params);

                if (mIsDebug)
                {
                    Log.i(getDebugTag(), "newSize:" + newSize);
                }
            }
        } else
        {
            if (start > 0 && end < parentSize)
            {
                if (layoutParamsSize != ViewGroup.LayoutParams.WRAP_CONTENT)
                {
                    setLayoutParamsSize(params, ViewGroup.LayoutParams.WRAP_CONTENT);
                    popView.setLayoutParams(params);

                    if (mIsDebug)
                    {
                        Log.e(getDebugTag(), "newSize:WRAP_CONTENT");
                    }
                }
            }
        }
    }

    private String getDebugTag()
    {
        return getClass().getSimpleName();
    }

    protected abstract int getParentSize(View popViewParent);

    protected abstract int getStartBound(View popView);

    protected abstract int getEndBound(View popView);

    protected abstract int getPopSize(View popView);

    protected abstract int getLayoutParamsSize(ViewGroup.LayoutParams params);

    protected abstract void setLayoutParamsSize(ViewGroup.LayoutParams params, int fixSize);
}