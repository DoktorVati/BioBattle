package com.biobattle;

import android.widget.ImageView;

public class Tower {
    private ImageView imageView;
    public float attackDamage = 10f;
    public float attackSpeed = 1f;
    public float attackRange = 5f;
    public float upgradePercentage = 0.75f;
    private int totalUpgrades = 0;

    public Tower(ImageView imageView) {
        this.imageView = imageView;
    }

    public void upgrade() {
        float damageMultiplier = 1 + totalUpgrades * upgradePercentage;
        float speedMultiplier = 1 + totalUpgrades;
        float rangeMultiplier = 1 + totalUpgrades * (upgradePercentage / 2);

        attackDamage *= damageMultiplier;
        attackSpeed *= speedMultiplier;
        attackRange *= rangeMultiplier;

        totalUpgrades += 1;
    }

    public ImageView getImageView() {
        return imageView;
    }
}
