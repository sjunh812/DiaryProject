package org.sjhstudio.diary.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.sjhstudio.diary.AlarmActivity;
import org.sjhstudio.diary.BackupActivity;
import org.sjhstudio.diary.DarkModeActivity;
import org.sjhstudio.diary.FontActivity;
import org.sjhstudio.diary.PasswordSettingsActivity;
import org.sjhstudio.diary.R;
import org.sjhstudio.diary.helper.AppHelper;
import org.sjhstudio.diary.helper.MyTheme;
import org.sjhstudio.diary.note.NoteDatabaseCallback;

public class OptionFragment extends Fragment {
    /** 상수 **/
    public static final int REQUEST_FONT_CHANGE = 101;
    public static final int REQUEST_ALARM_SETTING = 102;
    public static final int REQUEST_DARK_MODE = 103;

    /** data **/
    private NoteDatabaseCallback callback;  // DB 접근을 위한 callback
    private int curFontIndex = 0;           // 현재 선택한 폰트종류

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof NoteDatabaseCallback) callback = (NoteDatabaseCallback)context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if(callback != null) callback = null;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_option, container, false);

        setTitleTextView(rootView);
        setCountTextView(rootView);
        setCurFontText(rootView);

        /* 폰트 설정 */
        RelativeLayout fontLayout = (RelativeLayout)rootView.findViewById(R.id.fontLayout);
        fontLayout.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), FontActivity.class);
            getActivity().startActivityForResult(intent, REQUEST_FONT_CHANGE);
        });

        /* 알림 설정 */
        RelativeLayout noticeLayout = (RelativeLayout)rootView.findViewById(R.id.noticeLayout);
        noticeLayout.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), AlarmActivity.class);
            getActivity().startActivityForResult(intent, REQUEST_ALARM_SETTING);
        });

        /* 다크모드 설정 */
        RelativeLayout darkmodeLayout = (RelativeLayout)rootView.findViewById(R.id.darkmodeLayout);
        darkmodeLayout.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), DarkModeActivity.class);
            getActivity().startActivityForResult(intent, REQUEST_DARK_MODE);
        });

        /* 잠금설정 */
        RelativeLayout passwordLayout = (RelativeLayout)rootView.findViewById(R.id.passwordlayout);
        passwordLayout.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), PasswordSettingsActivity.class);
            requireActivity().startActivity(intent);
        });

        /* 잠금해제 */
        RelativeLayout releasePasswordLayout = (RelativeLayout)rootView.findViewById(R.id.releasepasswordlayout);
        releasePasswordLayout.setOnClickListener(v -> {
            SharedPreferences pref = requireContext().getSharedPreferences(MyTheme.SHARED_PREFERENCES_NAME, Activity.MODE_PRIVATE);
            if(pref != null) {
               String password = pref.getString(MyTheme.PASSWORD, "");
               if(!password.equals("")) {
                   SharedPreferences.Editor editor = pref.edit();
                   editor.remove(MyTheme.PASSWORD);
                   editor.commit();

                   Toast.makeText(getContext(), "비밀번호가 해제되었습니다.", Toast.LENGTH_SHORT).show();
                   return;
               }
            }

            Toast.makeText(getContext(), "설정된 비밀번호가 없습니다.", Toast.LENGTH_SHORT).show();
        });

        /* 백업 및 복원하기 */
        RelativeLayout backupLayout = (RelativeLayout)rootView.findViewById(R.id.backupLayout);
        backupLayout.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), BackupActivity.class);
            startActivity(intent);
        });

        /* 스토어 리뷰 */
        RelativeLayout reviewLayout = (RelativeLayout)rootView.findViewById(R.id.reviewLayout);
        reviewLayout.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("market://details?id=" + getContext().getPackageName()));
            startActivity(intent);
        });

        /* 피드백 보내기 */
        RelativeLayout ideaLayout = (RelativeLayout)rootView.findViewById(R.id.ideaLayout);
        ideaLayout.setOnClickListener(v -> {
            AppHelper helper = new AppHelper(getContext());
            String[] email = {"sjunh812@naver.com"};
            String appVersion = helper.getVersionName();
            String osVersion = helper.getOsName();
            String modelName = helper.getModelName();
            String contents = "안녕하세요!\n" +
                    "소중한 의견을 보내주셔서 감사합니다 :)\n" +
                    "신중하게 검토 후 답변드리겠습니다.\n" +
                    "----------------------------\n" +
                    "앱 버전 : " + appVersion +
                    "\n기기명 : " + modelName +
                    "\n안드로이드 OS : " + osVersion +
                    "\n----------------------------\n";

            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("plain/Text");
            intent.putExtra(Intent.EXTRA_EMAIL, email);
            intent.putExtra(Intent.EXTRA_SUBJECT, "[" + getString(R.string.app_name) + "] " + getString(R.string.report));
            intent.putExtra(Intent.EXTRA_TEXT, contents);
            intent.setType("message/rfc822");
            startActivity(intent);
        });

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void setTitleTextView(View rootView) {
        /** animation **/
        // 타이틀 애니메이션
        Animation translateRightAnim = AnimationUtils.loadAnimation(getContext(), R.anim.translate_right_animation);
        translateRightAnim.setDuration(350);
        TextView titleTextView = (TextView)rootView.findViewById(R.id.titleTextView);
        titleTextView.startAnimation(translateRightAnim);
    }

    @SuppressLint("SetTextI18n")
    private void setCountTextView(View rootView) {
        TextView allCountTextView = (TextView) rootView.findViewById(R.id.allCountTextView);
        TextView starCountTextView = (TextView) rootView.findViewById(R.id.starCountTextView);
        // 작성한 일기 총 개수
        int allCount = callback.selectAllCount();
        // 즐겨찾기 총 개수
        int starCount = callback.selectStarCount();

        allCountTextView.setText(allCount + "개");
        starCountTextView.setText(starCount + "개");
    }

    @SuppressLint("SetTextI18n")
    private void setCurFontText(View rootView) {
        Log.d("LOG", "index : " + curFontIndex);
        SharedPreferences pref = requireContext().getSharedPreferences(MyTheme.SHARED_PREFERENCES_NAME, Activity.MODE_PRIVATE);
        if(pref != null) curFontIndex = pref.getInt(MyTheme.FONT_KEY, 0);
        /** UI **/
        TextView curFontTextView = rootView.findViewById(R.id.curFontTextView);
        switch(curFontIndex) {
            case 100:
                curFontTextView.setText("시스템 서체");
                break;
            case -1:
                curFontTextView.setText("THE얌전해진언니체");
                break;
            case 0:
                curFontTextView.setText("교보 손글씨체");
                break;
            case 1:
                curFontTextView.setText("점꼴체");
                break;
            case 2:
                curFontTextView.setText("넥슨 배찌체");
                break;
            case 3:
                curFontTextView.setText("미니콩다방체");
                break;
            case 4:
                curFontTextView.setText("꼬마나비체");
                break;
            case 5:
                curFontTextView.setText("심경하체");
                break;
            case 6:
                curFontTextView.setText("강원교육모두체");
                break;
            case 7:
                curFontTextView.setText("쿠키런체");
                break;
            case 8:
                curFontTextView.setText("온글잎 만두몽키체");
                break;
            case 9:
                curFontTextView.setText("온글잎 윤우체");
                break;
            case 10:
                curFontTextView.setText("코트라 희망체");
                break;
            case 11:
                curFontTextView.setText("ACC 어린이 마음고운체");
                break;
            default:
                curFontTextView.setText("THE얌전해진언니체");
                break;
        }
    }
}
