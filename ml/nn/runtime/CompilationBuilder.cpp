/*
 * Copyright (C) 2017 The Android Open Source Project
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

#define LOG_TAG "CompilationBuilder"

#include "CompilationBuilder.h"

#include "ExecutionBuilder.h"
#include "ExecutionPlan.h"
#include "Manager.h"
#include "ModelBuilder.h"
#include "Utils.h"

namespace android {
namespace nn {

CompilationBuilder::CompilationBuilder(const ModelBuilder* model) :
        mModel(model), mPartitioning(DeviceManager::get()->getPartitioning()) {
    VLOG(COMPILATION) << "CompilationBuilder::CompilationBuilder";
}

int CompilationBuilder::finish() {
    // Get the list of HAL devices.
    return finish(DeviceManager::get()->getDrivers());
}

int CompilationBuilder::finish(const std::vector<std::shared_ptr<Device>>& devices) {
    if (mFinished) {
        LOG(ERROR) << "ANeuralNetworksCompilation_finish called more than once";
        return ANEURALNETWORKS_BAD_STATE;
    }
    // TODO validate the rest

    mFinished = true;

    if (mPartitioning) {
        int n = mModel->partitionTheWork(devices, mPreference, &mPlan);
        switch (n) {
            case ANEURALNETWORKS_NO_ERROR:
                break;
            case ANEURALNETWORKS_UNEXPECTED_NULL:
            case ANEURALNETWORKS_BAD_DATA:
                // The two error codes above should only be used for errors in the user's
                // request. In case of a user error, we won't try any fallback.
                // TODO: Document this in NeuralNetworks.h and in the HAL. Make sure
                // driver writers know which code they can return.
                return n;
            default:
                // The error might be recoverable. Return the error only if falling back
                // is not allowed.
                if (!DeviceManager::partitioningAllowsFallback(mPartitioning)) {
                    return n;
                }
                if (mModel->hasOEMOperation()) {
                    LOG(ERROR) << "Because of OEM op cannot fall back to CPU";
                    return n;
                }
                break;
        }
    }

    return ANEURALNETWORKS_NO_ERROR;
}

int CompilationBuilder::setPreference(int32_t preference) {
    if (mFinished) {
        LOG(ERROR) <<
                "ANeuralNetworksCompilation_setPreference can't modify after compilation finished";
        return ANEURALNETWORKS_BAD_STATE;
    }
    if (preference >= kNumberOfPreferences) {
        LOG(ERROR) << "ANeuralNetworksCompilation_setPreference invalid preference " << preference;
        return ANEURALNETWORKS_BAD_DATA;
    }

    mPreference = preference;
    return ANEURALNETWORKS_NO_ERROR;
}

int CompilationBuilder::setPartitioning(uint32_t partitioning) {
    if (mFinished) {
        LOG(ERROR) <<
                "ANeuralNetworksCompilation_setPartitioning can't modify after compilation finished";
        return ANEURALNETWORKS_BAD_STATE;
    }

    mPartitioning = partitioning;
    return ANEURALNETWORKS_NO_ERROR;
}

int CompilationBuilder::createExecution(ExecutionBuilder **execution) {
    if (!mFinished) {
        LOG(ERROR) << "ANeuralNetworksExecution_create passed an unfinished compilation";
        *execution = nullptr;
        return ANEURALNETWORKS_BAD_STATE;
    }
    *execution = new (std::nothrow) ExecutionBuilder(this);
    return (*execution ? ANEURALNETWORKS_NO_ERROR : ANEURALNETWORKS_OUT_OF_MEMORY);
}

}  // namespace nn
}  // namespace android
