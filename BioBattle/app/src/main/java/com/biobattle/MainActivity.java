private void manipulateOpacity(RelativeLayout textbox) {
        final Handler handler = new Handler();
        View textBox = findViewById(R.id.textbox);
        textBox.setVisibility(View.VISIBLE);
        final int delay = 500; // 0.5-second delay for each iteration
        final int totalIterationsIncrease = 6; // 3 seconds divided by 0.5 seconds per iteration
        final int totalIterationsDecrease = 14; // 7 seconds divided by 0.5 seconds per iteration

        // Gradually increase the opacity
        handler.postDelayed(new Runnable() {
            int iterations = 0;

            @Override
            public void run() {
                if (iterations < totalIterationsIncrease) {
                    float alphaValue = iterations * 1.0f / totalIterationsIncrease;
                    textBox.setAlpha(alphaValue);
                    iterations++;
                    handler.postDelayed(this, delay);
                } else {
                    textBox.setAlpha(1);
                    // Maintain opacity at 100% for 3 seconds
                    handler.postDelayed(() -> reverseOpacity(textbox), 3000);
                }
            }
        }, delay);
    }

    private void reverseOpacity(RelativeLayout textbox) {
        final Handler handler = new Handler();
        View textBox = findViewById(R.id.textbox);
        final int delay = 500; // 0.5-second delay for each iteration
        final int totalIterationsDecrease = 14; // 7 seconds divided by 0.5 seconds per iteration

        // Gradually decrease the opacity
        handler.postDelayed(new Runnable() {
            int iterations = totalIterationsDecrease;

            @Override
            public void run() {
                if (iterations > 0) {
                    float alphaValue = iterations * 1.0f / totalIterationsDecrease;
                    textBox.setAlpha(alphaValue);
                    iterations--;
                    handler.postDelayed(this, delay);
                } else {
                    // Set the textbox to invisible after 7 seconds
                    textBox.setVisibility(View.INVISIBLE);
                }
            }
        }, delay);
    }
