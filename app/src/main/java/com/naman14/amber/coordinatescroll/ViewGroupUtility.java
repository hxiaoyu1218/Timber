package com.naman14.amber.coordinatescroll;

import android.os.Build;
import android.view.View;
import android.view.ViewGroup;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class ViewGroupUtility {

    @NotNull
    public static List<View> getOrderedChildren(@NotNull ViewGroup viewGroup) {
        final ArrayList<View> preorderedList = buildTouchDispatchChildList(viewGroup);
        final boolean customOrder = preorderedList == null && isChildrenDrawingOrderEnabled(viewGroup);
        final int count = viewGroup.getChildCount();
        final List<View> orderedChildren = new ArrayList<>(count);
        for (int i = count - 1; i >= 0; i--) {
            final int childIndex = getAndVerifyPreorderedIndex(viewGroup, count, i, customOrder);
            final View child = getAndVerifyPreorderedView(viewGroup, preorderedList, childIndex);
            if (child != null) {
                orderedChildren.add(child);
            }
        }

        return orderedChildren;
    }

    private static View getAndVerifyPreorderedView(ViewGroup viewGroup, ArrayList<View> preorderedList, int childIndex) {
        final View child;
        if (preorderedList != null) {
            child = preorderedList.get(childIndex);
            if (child == null) {
                throw new RuntimeException("Invalid preorderedList contained null child at index "
                        + childIndex);
            }
        } else {
            child = viewGroup.getChildAt(childIndex);
        }
        return child;
    }

    private static boolean isChildrenDrawingOrderEnabled(ViewGroup viewGroup) {
        try {
            Class<? extends ViewGroup> clazz = viewGroup.getClass();
            Method method = clazz.getMethod("isChildrenDrawingOrderEnabled");
            return (boolean) method.invoke(viewGroup);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return false;
    }

    private static ArrayList<View> buildTouchDispatchChildList(ViewGroup viewGroup) {
        Class<? extends ViewGroup> clazz = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            try {
                if (clazz == null) {
                    clazz = viewGroup.getClass();
                }
                Method method = clazz.getMethod("buildTouchDispatchChildList");
                boolean accessable = method.isAccessible();
                if (!accessable) {
                    method.setAccessible(true);
                }
                ArrayList<View> list = (ArrayList<View>) method.invoke(viewGroup);
                if (!accessable) {
                    method.setAccessible(false);
                }
                return list;
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            try {
                if (clazz == null) {
                    clazz = viewGroup.getClass();
                }
                Method method = clazz.getMethod("buildOrderedChildList");
                boolean accessable = method.isAccessible();
                if (!accessable) {
                    method.setAccessible(true);
                }
                ArrayList<View> list = (ArrayList<View>) method.invoke(viewGroup);
                if (!accessable) {
                    method.setAccessible(false);
                }
                return list;
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private static int getAndVerifyPreorderedIndex(ViewGroup viewGroup, int childrenCount, int i, boolean customOrder) {
        final int childIndex;
        if (customOrder) {
            final int childIndex1 = getChildDrawingOrder(viewGroup, childrenCount, i);
            if (childIndex1 >= childrenCount) {
                throw new IndexOutOfBoundsException("getChildDrawingOrder() "
                        + "returned invalid index " + childIndex1
                        + " (child count is " + childrenCount + ")");
            }
            childIndex = childIndex1;
        } else {
            childIndex = i;
        }
        return childIndex;
    }

    private static int getChildDrawingOrder(ViewGroup viewGroup, int childrenCount, int i) {
        Class<? extends ViewGroup> clazz = viewGroup.getClass();
        try {
            Method method = clazz.getMethod("getChildDrawingOrder", int.class, int.class);
            return (int) method.invoke(viewGroup, childrenCount, i);
        } catch (Exception e) {
            e.printStackTrace();
            return i;
        }
    }

}