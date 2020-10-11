package com.codefusiongroup.gradshub.events;


import com.codefusiongroup.gradshub.common.BasePresenter;
import com.codefusiongroup.gradshub.common.models.User;

import java.util.List;
import java.util.Map;


//public class SchedulePresenter implements BasePresenter<ScheduleContract.IScheduleView>, ScheduleContract.ISchedulePresenter {
//
//
//    private final String  SUCCESS_CODE = "1";
//    private final String  SERVER_FAILURE_CODE = "-100";
//
//    private String mUserFavouredEventsResponseCode = null;
//    private String mUserFavouredEventsResponseMsg = null;
//    private String mEventsStarsResponseCode = null;
//
//    private List<String> mUserFavouredEvents;
//    private Map<String, String> mEventsStars;
//
//    private User mUser;
//
//    private boolean mUserFavouredEventsRequestFinished;
//    private boolean mEventsStarsRequestFinished;
//
//
//    private final ScheduleContract.IScheduleModel mScheduleModel = ScheduleModel.newInstance(this);
//    private ScheduleContract.IScheduleView mScheduleView;
//
//
//    @Override
//    public void onViewCreated(User user) {
//        mUser = user;
//        mScheduleModel.getUserFavouredEvents(user);
//        mScheduleModel.getEventsStars();
//    }
//
//
//    @Override
//    public void setUserFavouredEventsRequestFinished(boolean value) {
//        mUserFavouredEventsRequestFinished = value;
//    }
//
//
//    @Override
//    public void setEventsStarsRequestFinished(boolean value) {
//        mEventsStarsRequestFinished = value;
//    }
//
//
//    @Override
//    public void scheduleDataRequestFinished() {
//
//        // check only when both network requests have finished
//        if ( mUserFavouredEventsRequestFinished && mEventsStarsRequestFinished ) {
//
//            if (mScheduleView != null) {
//
//                // if there's a failure to contact the server in one of the requests
//                String API_CODE = "0";
//                if( mUserFavouredEventsResponseCode.equals(SERVER_FAILURE_CODE) || mEventsStarsResponseCode.equals(SERVER_FAILURE_CODE) ) {
//                    mScheduleView.showServerErrorResponse("failed to correctly update schedule, please swipe to refresh page.");
//                }
//
//                // if the user has not favoured any events but there exists favoured events in the DB for which we can get
//                // their star counts
//                else if ( mUserFavouredEventsResponseCode.equals(API_CODE) && mEventsStarsResponseCode.equals(SUCCESS_CODE) ) {
//                    mScheduleView.updateEventsSchedule(mUserFavouredEvents, mEventsStars);
//                    // show message that user has not favoured any events
//                    mScheduleView.showUserFavouredEventsStatus(mUserFavouredEventsResponseMsg);
//                }
//
//                // if the user has favoured some events then obviously we can retrieve the star counts
//                // NOTE: stars for events can only be retrieved if there are favoured events.
//                // reason for checking if stars response code is SUCCESS_CODE is because the request can fail which we handle
//                // above
//                else if ( mUserFavouredEventsResponseCode.equals(SUCCESS_CODE) && mEventsStarsResponseCode.equals(SUCCESS_CODE)  ) {
//                    mScheduleView.updateEventsSchedule(mUserFavouredEvents, mEventsStars);
//                }
//
//            }
//
//        }
//
//    }
//
//
//    @Override
//    public void registerUserFavouredEvents(User user, List<String> favouredEvents) {
//        mScheduleModel.registerFavouredEvents(user, favouredEvents);
//    }
//
//
//    @Override
//    public void unRegisterUserFavouredEvents(User user, List<String> unFavouredEvents) {
//        mScheduleModel.unRegisterFavouredEvents(user, unFavouredEvents);
//
//    }
//
//
//    @Override
//    public void onRequestUpdateUserFavouredEventsFinished() {
//
//        if ( mUserFavouredEventsResponseCode.equals(SUCCESS_CODE) ) {
//            mScheduleView.showUserFavouredEventsStatus(mUserFavouredEventsResponseMsg);
//        }
//
//        else if ( mUserFavouredEventsResponseCode.equals(SERVER_FAILURE_CODE) ) {
//            mScheduleView.showServerErrorResponse("failed to update your favoured events, please try again later");
//        }
//
//    }
//
//
//    @Override
//    public void setUserFavouredEvents(List<String> favouredEvents) {
//        mUserFavouredEvents = favouredEvents;
//    }
//
//
//    @Override
//    public void setEventsStars(Map<String, String> eventsStars) {
//        mEventsStars  = eventsStars;
//    }
//
//
//    @Override
//    public void setUserFavouredEventsResponseCode(String responseCode) {
//        mUserFavouredEventsResponseCode = responseCode;
//    }
//
//
//    @Override
//    public void setUserFavouredEventsResponseMsg(String responseMsg) {
//        mUserFavouredEventsResponseMsg = responseMsg;
//    }
//
//
//    @Override
//    public void setEventsStarsResponseCode(String responseCode) {
//        mEventsStarsResponseCode = responseCode;
//    }
//
//
//    @Override
//    public void subscribe(ScheduleContract.IScheduleView view) {
//        mScheduleView = view;
//    }
//
//
//    @Override
//    public void unsubscribe() {
//        mScheduleView = null;
//    }
//
//
//}
