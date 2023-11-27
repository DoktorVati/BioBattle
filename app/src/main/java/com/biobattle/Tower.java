package com.biobattle;

import android.util.Log;

import android.widget.ImageView;

public class Tower {
    private int towerNumber;
    private ImageView imageView;
    private float attackDamage;
    private float attackSpeed;
    private float attackRange;
    private float upgradePercentage = 1.15f;
    private int totalUpgrades = 0;
    private AttackRangeView attackRangeView;
    private float newAttackRange, newAttackDamage, newAttackSpeed;
    public Tower(ImageView imageView, float attackRange, float attackDamage, float attackSpeed)
    {
        this.imageView = imageView;
        this.attackRange = attackRange;
        this.attackDamage = attackDamage;
        this.attackSpeed = attackSpeed;
    }
    public void setTowerNumber(int towerNumber) {
        this.towerNumber = towerNumber;
    }

    public void upgrade(int imageResource) {
        newAttackRange = attackRange * upgradePercentage;
        newAttackDamage = attackDamage * upgradePercentage;
        newAttackSpeed = attackSpeed * upgradePercentage;

        attackRange = (newAttackRange *= upgradePercentage);
        attackDamage = (newAttackDamage *= upgradePercentage);
        attackSpeed = (newAttackSpeed *= upgradePercentage);
        totalUpgrades ++;
    }
    public ImageView getImageView()
    {
        return imageView;
    }
    public float getAttackRange()
    {
        return this.attackRange;
    }
    public void setAttackRange(float attackRange)
    {
        this.attackRange = attackRange;
    }
    public float getDamage()
    {
        return this.attackDamage;
    }
    public void setDamage(float attackDamage)
    {
        this.attackDamage = attackDamage;
    }
    public float getAttackSpeed()
    {
        return attackSpeed;
    }
    public void setAttackSpeed(float attackSpeed)
    {
        this.attackSpeed = attackSpeed;
    }
    public float getTotalUpgrades()
    {
        if (imageView == null) {
            return totalUpgrades; // Return default value for towers without an image view
        } else if (imageView.getTag().equals(R.drawable.simpletower)) {
            return totalUpgrades * 1;
        } else if (imageView.getTag().equals(R.drawable.golgitower)) {
            return totalUpgrades * 1.5f;
        } else if (imageView.getTag().equals(R.drawable.cannontower)) {
            return totalUpgrades * 2.25f;
        } else if (imageView.getTag().equals(R.drawable.killertframe1)) {
            return totalUpgrades * 3;
        } else {
            return totalUpgrades; // Default multiplier for unrecognized towers
        }
    }
    public boolean hasReachedMaxUpgrades(boolean upgradeMenu) {
        int maxUpgrades = 3; // Maximum upgrades allowed
        if (imageView == null) {
            return totalUpgrades >= maxUpgrades; // Return true if upgrades reach the limit for towers without an image view
        } else if (imageView.getTag().equals(R.drawable.simpletower)) {
            if(upgradeMenu)
                return totalUpgrades >= 5;
            return totalUpgrades >= 6;
        } else if (imageView.getTag().equals(R.drawable.golgitower)) {
            if(upgradeMenu)
                return totalUpgrades >= 4;
            return totalUpgrades >= 5;
        } else if (imageView.getTag().equals(R.drawable.cannontower)) {
            if(upgradeMenu)
                return totalUpgrades >= 3;
            return totalUpgrades >= 4;
        } else if (imageView.getTag().equals(R.drawable.killertframe1)) {
            if(upgradeMenu)
                return totalUpgrades >= 2;
            return totalUpgrades >= 3;
        }
        else
        {
            return totalUpgrades >= 3;
        }
    }

}
