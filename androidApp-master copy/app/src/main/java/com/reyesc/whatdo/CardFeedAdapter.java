package com.reyesc.whatdo;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class CardFeedAdapter extends RecyclerView.Adapter<CardViewHolder> implements CardTouchHelper.CardTouchHelperListener {
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private List<ActivityCard> cardList;
    private int visibleThreshold = 3;
    private int loadCount = 10;
    private boolean loading;
    private FragmentExtension.FragmentToActivityListener fragmentToActivityListener;

    public CardFeedAdapter(SwipeRefreshLayout swipeRefreshLayout, RecyclerView recyclerView, ArrayList<ActivityCard> cardList, FragmentExtension.FragmentToActivityListener fragmentToActivityListener){
        this.swipeRefreshLayout = swipeRefreshLayout;
        this.recyclerView = recyclerView;
        this.cardList = cardList;
        this.fragmentToActivityListener = fragmentToActivityListener;

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshCardFeed();
            }
        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                loadCardFeed();
            }
        });

        ItemTouchHelper.SimpleCallback cardTouchHelperCallback = new CardTouchHelper(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT, this);
        new ItemTouchHelper(cardTouchHelperCallback).attachToRecyclerView(recyclerView);

        loadMoreCards();
    }

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.activity_card, parent, false);
        return new CardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {
        ActivityCard activityCard = cardList.get(position);

        //holder.imageView.setImageDrawable(context.getDrawable(activityCard.getImage()));
        holder.getTextViewDate().setText(activityCard.getDate());
        holder.getTextViewTitle().setText(activityCard.getTitle());
        holder.getTextViewTags().setText(activityCard.getTags());
        holder.getTextViewDescription().setText(activityCard.getDescription());
    }

    @Override
    public int getItemCount() {
        return cardList.size();
    }

    protected void removeCard(ActivityCard card) {
        int position = cardList.indexOf(card);
        cardList.remove(position);
//        recyclerView.removeViewAt(position);
        this.notifyItemRemoved(position);
    }

    protected void restoreCard(ActivityCard card) {
        int position;
        position = 0;
        while (position < cardList.size() && card.getId() > cardList.get(position).getId()) {
            position++;
        }

        if (position < cardList.size()) {
            cardList.add(position, card);
            this.notifyItemInserted(position);
        } else {
            cardList.add(card);
            this.notifyItemInserted(cardList.size() - 1);
        }
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if (viewHolder instanceof CardViewHolder) {
            String name = cardList.get(viewHolder.getAdapterPosition()).getTitle();

            final ActivityCard deletedItem = cardList.get(viewHolder.getAdapterPosition());

            Snackbar snackbar;
            if (direction == ItemTouchHelper.LEFT) {
                removeCard(deletedItem);
                snackbar = Snackbar.make(swipeRefreshLayout, name + " removed from list!", Snackbar.LENGTH_LONG);
                snackbar.setAction("UNDO", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        restoreCard(deletedItem);
                    }
                });
            } else {
                fragmentToActivityListener.fromFeedToCollection(deletedItem);
                snackbar = Snackbar.make(swipeRefreshLayout, name + " saved for later!", Snackbar.LENGTH_LONG);
                snackbar.setAction("UNDO", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        fragmentToActivityListener.fromCollectionToFeed(deletedItem);
                    }
                });
            }
            snackbar.setActionTextColor(Color.YELLOW);
            snackbar.show();
        }
    }

    private void refreshCardFeed(){
        fragmentToActivityListener.toasting("Refreshing");
        this.notifyItemInserted(cardList.size());
        swipeRefreshLayout.setRefreshing(false);
    }

    private void loadCardFeed() {
        LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        int totalItemCount = layoutManager.getItemCount();
        int lastVisibleItem = layoutManager.findLastVisibleItemPosition();
        if (!loading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
            // End has been reached, do something
            fragmentToActivityListener.toasting("More cards loaded");
            loading = true;
            loadMoreCards();
        }
    }

    private void loadMoreCards(){
        for(int i = 0; i < loadCount; i++){
            cardList.add(new ActivityCard((cardList.size()),0,"Date\n31", "Title" + (cardList.size()), "Tags", "Description"));
        }
        this.notifyItemInserted(cardList.size());
        loading = false;
    }
}
