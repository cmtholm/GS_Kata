package com.gs.collections.kata;

import java.util.List;

import com.gs.collections.api.block.procedure.Procedure;
import com.gs.collections.api.map.MutableMap;
import com.gs.collections.api.multimap.list.MutableListMultimap;
import com.gs.collections.impl.list.mutable.FastList;
import com.gs.collections.impl.map.mutable.UnifiedMap;
import com.gs.collections.impl.test.Verify;
import com.gs.collections.impl.utility.ArrayIterate;
import org.junit.Assert;
import org.junit.Test;

public class Exercise8Test extends CompanyDomainForKata
{
    /**
     * Create a multimap where the keys are the names of cities and the values are the customers from those cities.
     */
    @Test
    public void customersByCity()
    {
        // Notice that the second generic type is Customer, not List<Customer>
        MutableListMultimap<String, Customer> multimap = null;

        Assert.assertEquals(FastList.newListWith(this.company.getCustomerNamed("Mary")), multimap.get("Liphook"));
        Assert.assertEquals(
                FastList.newListWith(
                        this.company.getCustomerNamed("Fred"),
                        this.company.getCustomerNamed("Bill")),
                multimap.get("London"));
    }

    @Test
    public void mapOfItemsToSuppliers()
    {
        /**
         * Change itemsToSuppliers to a MutableMultimap<String, Supplier>
         */
        final MutableMap<String, List<Supplier>> itemsToSuppliers = UnifiedMap.newMap();

        ArrayIterate.forEach(this.company.getSuppliers(), new Procedure<Supplier>()
        {
            @Override
            public void value(final Supplier supplier)
            {
                ArrayIterate.forEach(supplier.getItemNames(), new Procedure<String>()
                {
                    @Override
                    public void value(String itemName)
                    {
                        Assert.fail("Refactor this as part of Exercise 6");

                        List<Supplier> suppliersForItem;
                        if (itemsToSuppliers.containsKey(itemName))
                        {
                            suppliersForItem = itemsToSuppliers.get(itemName);
                        }
                        else
                        {
                            suppliersForItem = FastList.newList();
                            itemsToSuppliers.put(itemName, suppliersForItem);
                        }

                        suppliersForItem.add(supplier);
                    }
                });
            }
        });
        Verify.assertIterableSize("should be 2 suppliers for sofa", 2, itemsToSuppliers.get("sofa"));
    }

    @Test
    public void reminder()
    {
        Assert.fail("Refactor setUpCustomersAndOrders() in the super class to not have so much repetition.");
        // Delete this whole method when you're done. It's just a reminder.
    }
}