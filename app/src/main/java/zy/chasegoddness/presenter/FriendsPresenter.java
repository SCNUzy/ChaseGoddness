package zy.chasegoddness.presenter;

import android.graphics.Bitmap;
import android.support.annotation.Nullable;
import android.support.v4.util.LruCache;
import android.util.Log;
import android.widget.ImageView;

import java.util.List;

import cn.bmob.v3.BmobUser;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import zy.chasegoddness.model.FriendsLoginModel;
import zy.chasegoddness.model.FriendsModel;
import zy.chasegoddness.model.bean.FriendsContent;
import zy.chasegoddness.model.bean.User;
import zy.chasegoddness.ui.activity.FriendsActivity;
import zy.chasegoddness.ui.activity.iactivity.IFriendsView;

/**
 * 分享圈的控制器
 */
public class FriendsPresenter {

    private IFriendsView view;
    private User currentUser;
    private int pageNum = 1;
    private int pageSize = 10;

    public FriendsPresenter(FriendsActivity view) {
        this.view = view;
    }

    public void init() {
        checkForLogin();
        refresh();
    }

    /**
     * 刷新最新的分享圈事件
     */
    public void refresh() {
        view.setRefreshing(true);
        pageNum = 1;
        FriendsModel.getFriendsContent(pageNum, pageSize)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(friendsContents -> {
                    if (friendsContents.size() == 0)
                        view.showToast("一条数据都没有哦");
                    view.setList(friendsContents);
                }, throwable -> {
                    Log.e("zy", "Friends Presenter get FriendsContent error: " + throwable.toString());
                    view.setRefreshing(false);
                    view.showToast("一个可怕的错误发生了");
                }, () -> {
                    view.setRefreshing(false);
                    view.notifyChanged();
                });
    }

    /**
     * 加载更多的分享圈事件
     */
    public void loadMore() {
        view.setRefreshing(true);
        pageNum++;
        FriendsModel.getFriendsContent(pageNum, pageSize)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(friendsContents -> {
                    if (friendsContents.size() == 0) {
                        view.showToast("已经到底了");
                    } else {
                        view.updateList(friendsContents);
                        view.notifyChanged();
                    }
                }, throwable -> {
                    view.showToast("一个可怕的错误发生了");
                    view.setRefreshing(false);
                    Log.e("zy", "Friends Presenter get FriendsContent error: " + throwable.toString());
                }, () -> view.setRefreshing(false));
    }

    /**
     * 如果没登陆 弹出登录对话框
     */
    public boolean checkForLogin() {
        if (currentUser != null) return true;

        currentUser = BmobUser.getCurrentUser(User.class);
        if (currentUser != null) {
            return true;
        } else {
            FriendsLoginModel.showDialog(view.getSupportFragmentManager());
            return false;
        }
    }

    /**
     * 打开查看大图界面
     */
    public void showBigImage(@Nullable Bitmap bitmap, String url) {

    }
}