package com.weplay.game;

import android.app.Activity;
import android.os.Bundle;

import com.weplay.WePlay;
import com.weplay.ad.AdType;
import com.weplay.game.widget.GameView;
import com.weplay.message.WePlayUser;


public class GameActivity extends Activity {
    private GameView gameView;
    private String adId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gameView = new GameView(this);
        setContentView(gameView);
        //WePlay.sendScore(long score)
        gameView.setOnScoreCallback(WePlay::sendScore);
        gameView.setGreatMomentCallback(WePlay::greatMoment);
        gameView.setAdCallback(new GameView.OnShowAdCallback() {
            @Override
            public void showInterstitialAd() {
                WePlay.showAd(AdType.TYPE_INTERSTITIAL, null);
            }

            @Override
            public void showRewardedVideoAd() {
                adId = String.valueOf(System.currentTimeMillis());
                WePlay.showAd(AdType.TYPE_REWARDED_VIDEO, adId);
            }
        });
        WePlay.setOnAdCallback(adId -> {
            if (adId.equals(GameActivity.this.adId)) {
                GameActivity.this.adId = null;
                //give the gift
                gameView.addBomb();
            }
        });
        gameView.start();
        WePlay.setOnAccountCallback(new WePlay.OnAccountCallback() {
            @Override
            public boolean onLogin(WePlayUser wePlayUser) {
                gameView.setUserName(wePlayUser.userName + "@WePlay");
                gameView.start();
                return true;
            }

            @Override
            public boolean onLogout() {
                gameView.setUserName(null);
                gameView.start();
                gameView.postInvalidate();
                return true;
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (gameView != null)
            gameView.resume();
    }

    @Override
    protected void onPause() {
        if (gameView != null)
            gameView.pause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if (gameView != null)
            gameView.destroy();
        gameView = null;
        super.onDestroy();
    }
}