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

    //optional in case we want to have a limit on upgrades, currently Infinite
    public int getTotalUpgrades()
    {
        return totalUpgrades;
    }
}
