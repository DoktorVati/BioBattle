<RelativeLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:backgroundTintMode="src_atop"
    android:backgroundTint="@color/invisible"
    android:background="@drawable/main_menu_background">

    <ImageView
        android:id ="@+id/mainMenu"
        android:layout_width="wrap_content"
        android:layout_height="320dp"
        android:layout_alignParentTop="false"
        android:contentDescription="@string/menuContentDescription"
        android:src="@drawable/bio_battle_title" />

    <ImageButton
        android:id="@+id/playButton"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_below="@id/mainMenu"
        android:layout_centerHorizontal="true"
        android:layout_marginTop="-100dp"
        android:layout_marginBottom="40dp"
        android:text="@string/play_text"
        android:onClick="playGame"
        android:src="@drawable/playbutton1"
        android:background="@color/invisible"
        android:scaleY="0.5"
        android:scaleX="0.5"
        android:contentDescription="@string/play_text"
        />

    <ImageView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:src="@drawable/simpletower"
        android:layout_alignParentEnd="true"
        android:layout_alignParentBottom="true"
        android:layout_marginEnd="150dp"
        android:layout_marginBottom="255dp" />

    <ImageView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:src="@drawable/golgitower"
        android:layout_alignParentStart="true"
        android:layout_alignParentBottom="true"
        android:layout_marginStart="150dp"
        android:layout_marginBottom="255dp"
        android:scaleType="center"
        android:scaleY="2"
        android:scaleX="2"/>
    <ImageView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:src="@drawable/killeridleanim"
        android:layout_alignParentEnd="true"
        android:layout_alignParentBottom="true"
        android:layout_marginEnd="130dp"
        android:layout_marginBottom="90dp"
        android:scaleX="1.2"
        android:scaleType="center"
        android:scaleY = "1.2"/>

    <ImageView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:src="@drawable/cannontower"
        android:layout_alignParentStart="true"
        android:layout_alignParentBottom="true"
        android:layout_marginStart="150dp"
        android:layout_marginBottom="105dp"
        android:scaleType="center"
        android:scaleY="1.2"
        android:scaleX="1.2"/>
    <!-- Add other menu items as needed -->
</RelativeLayout>
