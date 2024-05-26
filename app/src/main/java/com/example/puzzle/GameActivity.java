package com.example.puzzle;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.AdapterView;
import android.view.View;
import android.content.Context;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.Collections;
import androidx.core.content.ContextCompat;

public class GameActivity extends AppCompatActivity {

    GridView gridView;
    ArrayList<Bitmap> tiles = new ArrayList<>(16); 
    ImageAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        gridView = findViewById(R.id.grid);
        gridView.setNumColumns(4);  
        initializeTiles();

        adapter = new ImageAdapter(this, tiles);
        gridView.setAdapter(adapter);

        shuffleTiles();  

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                moveTile(position);
                validateGrid();  
            }
        });

        Button exitGameButton = findViewById(R.id.exitGameButton);
        exitGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); 
            }
        });
    }

    private void initializeTiles() {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 2; 
        Bitmap sourceImage = BitmapFactory.decodeResource(getResources(), R.drawable.smiling_emoji, options);
        if (sourceImage != null) {
            int pieceWidth = sourceImage.getWidth() / 4;
            int pieceHeight = sourceImage.getHeight() / 4;
            tiles.clear();

            for (int i = 0; i < 16; i++) {
                if (i < 15) {
                    int row = i / 4;
                    int col = i % 4;
                    Bitmap piece = Bitmap.createBitmap(sourceImage, col * pieceWidth, row * pieceHeight, pieceWidth, pieceHeight);
                    tiles.add(piece);
                } else {
                    tiles.add(null); 
                }
            }
        } else {
            Log.e("initializeTiles", "Failed to decode resource");
        }
    }

    private void shuffleTiles() {
        Collections.shuffle(tiles);
        validateGrid();
        adapter.notifyDataSetChanged();
    }

    private void moveTile(int position) {
        int emptyPos = tiles.indexOf(null);
        if ((position == emptyPos - 1 && position % 4 != 3) ||
                (position == emptyPos + 1 && position % 4 != 0) ||
                (position == emptyPos - 4) ||
                (position == emptyPos + 4)) {
            Collections.swap(tiles, position, emptyPos);
            adapter.notifyDataSetChanged();
        }
        validateGrid();
    }

    private void validateGrid() {
        if (tiles.size() != 16 || tiles.stream().filter(t -> t == null).count() != 1) {
            Log.e("validateGrid", "Grid integrity error detected. Correcting...");
            initializeTiles(); 
            adapter.notifyDataSetChanged();
        }
    }

    class ImageAdapter extends BaseAdapter {
        private Context context;
        private ArrayList<Bitmap> tiles;
        private int imageSize;

        ImageAdapter(Context context, ArrayList<Bitmap> tiles) {
            this.context = context;
            this.tiles = tiles;
            calculateImageSize();
        }

        private void calculateImageSize() {
            int screenWidth = context.getResources().getDisplayMetrics().widthPixels;
            imageSize = screenWidth / 4; 
        }

        @Override
        public int getCount() {
            return tiles.size();
        }

        @Override
        public Object getItem(int position) {
            return tiles.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageView = (convertView == null) ? new ImageView(context) : (ImageView) convertView;
            imageView.setLayoutParams(new GridView.LayoutParams(imageSize, imageSize)); 
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setBackground(ContextCompat.getDrawable(context, R.drawable.tile_border));
            imageView.setImageBitmap(tiles.get(position) != null ? tiles.get(position) : null);
            imageView.setVisibility(tiles.get(position) != null ? View.VISIBLE : View.INVISIBLE);
            return imageView;
        }
    }
}
