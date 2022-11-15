package com.example.suchetana.Activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;

import com.example.suchetana.Fragments.ArchiveFragment;
import com.example.suchetana.Fragments.HomeFragment;
import com.example.suchetana.Fragments.LibraryFragment;
import com.example.suchetana.R;
import com.example.suchetana.databinding.ActivityMainBinding;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    //view binding
    private ActivityMainBinding binding;

    //firebase auth
    private FirebaseAuth firebaseAuth;

    //handle back pressed
    int cnt = 0;

    @Override
    public void onBackPressed() {
        cnt++;

        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
            cnt = 0;
        } else {
            if (cnt > 1) {
                super.onBackPressed();
            } else {
                Toast.makeText(MainActivity.this, "Press once again to exit", Toast.LENGTH_SHORT).show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        cnt = 0;
                    }
                }, 2000);
            }

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //to set statusBar color to desired color
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.yellow01));
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        //init firebase auth
        firebaseAuth = FirebaseAuth.getInstance();

        //check user to set navigation menu accordingly
        checkUser();

        //at first set fragment view to home fragment
        getSupportFragmentManager().beginTransaction().replace(binding.appMain.fragmentContainer.getId(), new HomeFragment()).commit();

        //tint of getting selected
        binding.appMain.homeBtn.setColorFilter(getResources().getColor(R.color.gray));
        binding.appMain.libraryBtn.setColorFilter(getResources().getColor(R.color.black));
        binding.appMain.archiveBtn.setColorFilter(getResources().getColor(R.color.black));

        //handle click menuBtn: Open navigation drawer
        binding.appMain.menuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.drawerLayout.openDrawer(binding.navMenu);
            }
        });

        //handle click searchBtn: Open Search Activity
        binding.appMain.searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,SearchActivity.class);
                intent.putExtra("categoryId","");
                intent.putExtra("categoryTitle","");
                startActivity(intent);
            }
        });

        //handle click open cart
        binding.appMain.cartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (firebaseAuth.getCurrentUser() == null) {
                    Toast.makeText(MainActivity.this, "You're not logged in", Toast.LENGTH_SHORT).show();
                }
                else {
                    startActivity(new Intent(MainActivity.this, CartActivity.class));
                }
            }
        });

        //handle click homeBtn: Open home fragment
        binding.appMain.homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //selected fragment
                Fragment selectedFragment = new HomeFragment();
                //set view to frame layout
                getSupportFragmentManager().beginTransaction().replace(binding.appMain.fragmentContainer.getId(), selectedFragment).commit();

                //tint of getting selected
                binding.appMain.homeBtn.setColorFilter(getResources().getColor(R.color.gray));
                binding.appMain.libraryBtn.setColorFilter(getResources().getColor(R.color.black));
                binding.appMain.archiveBtn.setColorFilter(getResources().getColor(R.color.black));
            }
        });

        //handle click libraryBtn: Open library fragment
        binding.appMain.libraryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //selected fragment
                Fragment selectedFragment = new LibraryFragment();
                //set view to frame layout
                getSupportFragmentManager().beginTransaction().replace(binding.appMain.fragmentContainer.getId(), selectedFragment).commit();

                //tint of getting selected
                binding.appMain.homeBtn.setColorFilter(getResources().getColor(R.color.black));
                binding.appMain.libraryBtn.setColorFilter(getResources().getColor(R.color.gray));
                binding.appMain.archiveBtn.setColorFilter(getResources().getColor(R.color.black));
            }
        });

        //handle click archiveBtn: Open archive fragment
        binding.appMain.archiveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //selected fragment
                Fragment selectedFragment = new ArchiveFragment();
                //set view to frame layout
                getSupportFragmentManager().beginTransaction().replace(binding.appMain.fragmentContainer.getId(), selectedFragment).commit();

                //tint of getting selected
                binding.appMain.homeBtn.setColorFilter(getResources().getColor(R.color.black));
                binding.appMain.libraryBtn.setColorFilter(getResources().getColor(R.color.black));
                binding.appMain.archiveBtn.setColorFilter(getResources().getColor(R.color.gray));
            }
        });

        //handle navigation drawer activities
        binding.navMenu.setNavigationItemSelectedListener(navListener);
    }

    private void checkUser() {
        // get current user, if logged in
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        if (firebaseUser == null) {
            //user not logged in
            binding.navMenu.getMenu().clear();
            binding.navMenu.inflateMenu(R.menu.nav_menu);
        } else {
            // user logged in check user type, same as done in loginActivity
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
            ref.child(firebaseUser.getUid())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            // get user type
                            String userType = "" + snapshot.child("userType").getValue();
                            // check user type
                            if (userType.equals("user")) {
                                // this is simple user, set user navigation menu
                                binding.navMenu.getMenu().clear();
                                binding.navMenu.inflateMenu(R.menu.nav_menu_after_login_user);
                            } else if (userType.equals("admin")) {
                                // this is admin, set admin navigation menu
                                binding.navMenu.getMenu().clear();
                                binding.navMenu.inflateMenu(R.menu.nav_menu_after_login_admin);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError error) {

                        }
                    });
        }
    }

    //Navigation ItemSelectedListener
    private NavigationView.OnNavigationItemSelectedListener navListener =
            new NavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                    //get selected item id and open respected activity
                    switch (item.getItemId()) {
                        case R.id.menu_home:
                            startActivity(new Intent(MainActivity.this, MainActivity.class));
                            finish();
                            break;
                        case R.id.menu_login:
                            startActivity(new Intent(MainActivity.this, LoginActivity.class));
                            break;
                        case R.id.menu_logout:
                            firebaseAuth.signOut();
                            startActivity(new Intent(MainActivity.this, MainActivity.class));
                            finish();
                            break;
                        case R.id.menu_admin_panel:
                            startActivity(new Intent(MainActivity.this, DashboardAdminActivity.class));
                            break;
                        case R.id.menu_feedback:
                            startActivity(new Intent(MainActivity.this, ContactActivity.class));
                            break;
                        case R.id.menu_wishlist:
                            startActivity(new Intent(MainActivity.this, WishListActivity.class));
                            break;
                        case R.id.menu_about_us:
                            startActivity(new Intent(MainActivity.this, AboutUsActivity.class));
                            break;
                        case R.id.menu_profile:
                            startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                            break;
                        case R.id.menu_orders:
                            startActivity(new Intent(MainActivity.this, OrderActivity.class));
                            break;
                    }

                    return true;
                }
            };
}